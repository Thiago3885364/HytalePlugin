package com.example.xpbar;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.ui.Anchor;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class XpHud extends CustomUIHud {

    private static final int MAX_LEVEL = 100;

    // precisa bater com o @BarWidth no .ui
    private static final int BAR_WIDTH_PX = 220;

    // precisa bater com o @BarHeight no .ui
    private static final int BAR_HEIGHT_PX = 12;

    private int level = 0;

    public XpHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("XpBar/XpHud.ui");
        apply(builder, 0);
    }

    public void setLevel(int newLevel) {
        if (newLevel < 0) newLevel = 0;
        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;

        this.level = newLevel;

        UICommandBuilder builder = new UICommandBuilder();
        apply(builder, newLevel);

        update(false, builder);
    }

    private void apply(UICommandBuilder builder, int lvl) {
        double pct = Math.max(0.0, Math.min(1.0, lvl / (double) MAX_LEVEL));

        int fillWeight = (int) Math.round(pct * 100.0);
        int emptyWeight = 100 - fillWeight;

        // ✅ Atualiza propriedades simples (primitivas)
        builder.set("#XpFill.FlexWeight", fillWeight);
        builder.set("#XpEmpty.FlexWeight", emptyWeight);

        builder.set("#XpText.Text", "XP " + lvl + "/" + MAX_LEVEL);
    }

    /**
     * Cria um Anchor compatível com diferentes versões da API:
     * 1) tenta construtor (int,int,int,int)
     * 2) tenta new Anchor() + setters setLeft/setBottom/setWidth/setHeight
     */
    private static Anchor makeAnchor(int left, int bottom, int width, int height) {
        // 1) tenta construtor (int,int,int,int)
        try {
            Constructor<Anchor> c = Anchor.class.getConstructor(int.class, int.class, int.class, int.class);
            return c.newInstance(left, bottom, width, height);
        } catch (Throwable ignored) {
            // cai para setters
        }

        // 2) tenta construtor vazio + setters
        try {
            Anchor a = Anchor.class.getConstructor().newInstance();

            invokeSetter(a, "setLeft", int.class, left);
            invokeSetter(a, "setBottom", int.class, bottom);
            invokeSetter(a, "setWidth", int.class, width);
            invokeSetter(a, "setHeight", int.class, height);

            return a;
        } catch (Throwable t) {
            throw new RuntimeException("Não consegui criar Anchor nesta versão da API.", t);
        }
    }

    private static void invokeSetter(Object target, String methodName, Class<?> paramType, int value) throws Exception {
        Method m = target.getClass().getMethod(methodName, paramType);
        m.invoke(target, value);
    }
}