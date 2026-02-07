package com.example.xpbar;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

// NPCPlugin helper (mais fácil que montar Holder na mão)
import com.hypixel.hytale.server.npc.NPCPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SummonCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> typeArg =
            this.withRequiredArg("type", "summon.type", ArgTypes.STRING);

    private final RequiredArg<Integer> qtyArg =
            this.withRequiredArg("qty", "summon.qty", ArgTypes.INTEGER);

    // Model key do “golem branco do portal”
    // Esse ID aparece na lista de entidades (como model key), mesmo que /npc spawn (role) não aceite. [3](https://hytaleplugins.gg/)
    private static final String PORTAL_GOLEM_MODEL_KEY = "Golem_Crystal_Earth";

    public SummonCommand() {
        super("summon", "Uso: /summon golem <quantidade>");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> playerEntityRef,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        String type = typeArg.get(ctx);
        int qty = Math.max(1, qtyArg.get(ctx));

        if (!"golem".equalsIgnoreCase(type)) {
            ctx.sendMessage(Message.raw("Uso: /summon golem <quantidade>"));
            return;
        }

        // Executar spawn no thread do mundo é recomendado para mexer com store/world. [1](https://github.com/xLexih/HytalePlugin)
        world.execute(() -> {
            Vector3d basePos = getPlayerPositionSafe(store, playerEntityRef);

            // se por algum motivo não der pra pegar posição, avisa e aborta
            if (basePos == null) {
                playerRef.sendMessage(Message.raw("Não consegui obter posição do player (TransformComponent)."));
                return;
            }

            for (int i = 0; i < qty; i++) {
                Vector3d pos = new Vector3d(basePos.x + (i * 2.0), basePos.y, basePos.z);
                Vector3f rot = new Vector3f(0, 0, 0);

                // NPCPlugin.spawnNPC(store, "<ModelKey>", null, position, rotation) [2](https://deepwiki.com/Steamodded/smods/9.4-integrating-with-ui)
                var result = NPCPlugin.get().spawnNPC(store, PORTAL_GOLEM_MODEL_KEY, null, pos, rot);

                if (result == null) {
                    playerRef.sendMessage(Message.raw("Falha ao spawnar: " + PORTAL_GOLEM_MODEL_KEY));
                    return;
                }
            }

            playerRef.sendMessage(Message.raw("Spawnado " + qty + " golem(s) do portal!"));
        });
    }

    /**
     * Pega posição do jogador sem depender de um getter específico (varia por build).
     * Tenta:
     *  - transform.getPosition()
     *  - campo transform.position
     */
    private Vector3d getPlayerPositionSafe(Store<EntityStore> store, Ref<EntityStore> playerEntityRef) {
        try {
            // Padrão recomendado: pegar TransformComponent pelo EntityModule + store.getComponent(...) [1](https://github.com/xLexih/HytalePlugin)
            Object transform = store.getComponent(playerEntityRef, EntityModule.get().getTransformComponentType());

            if (transform == null) return null;

            // tenta getPosition()
            try {
                Method m = transform.getClass().getMethod("getPosition");
                Object out = m.invoke(transform);
                if (out instanceof Vector3d v) return v;
            } catch (NoSuchMethodException ignored) {}

            // tenta campo "position"
            try {
                Field f = transform.getClass().getField("position");
                Object out = f.get(transform);
                if (out instanceof Vector3d v) return v;
            } catch (NoSuchFieldException ignored) {}

            return null;

        } catch (Throwable t) {
            return null;
        }
    }
}