package com.example.xpbar;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class XpHud extends CustomUIHud {

    private static final int MAX_LEVEL = 100;

    // precisa bater com o @BarWidth no .ui
    private static final int BAR_WIDTH_PX = 220;

    private int level = 0;

    public XpHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        // Esse caminho aponta para resources/Common/UI/Custom/XpBar/XpHud.ui [3](https://hytale-docs.com/docs/api/server-internals/ui-reference)[2](https://hytalemodding.dev/en/docs/guides/plugin/ui)
        builder.append("Custom/XpBar/teste.ui");

        // estado inicial
        apply(builder, 0);
    }

    public void setLevel(int newLevel) {
        if (newLevel < 0) newLevel = 0;
        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;

        this.level = newLevel;

        UICommandBuilder builder = new UICommandBuilder();
        apply(builder, newLevel);

        // update(false, ...) = update incremental (mais leve). [1](https://deepwiki.com/vulpeslab/hytale-docs/9.3-hud-system)
        update(false, builder);
    }

    private void apply(UICommandBuilder builder, int lvl) {
        float pct = (float) lvl / (float) MAX_LEVEL;
        int fill = Math.round(BAR_WIDTH_PX * pct);

        // Ajusta a largura do preenchimento (barra azul)
        builder.set("#XpFill.Anchor.Width", fill);

        // Texto (opcional)
        builder.set("#XpText.Text", "XP " + lvl + "/" + MAX_LEVEL);
    }
}