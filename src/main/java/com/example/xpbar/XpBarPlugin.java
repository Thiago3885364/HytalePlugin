package com.example.xpbar;

import com.example.xpbar.XpHud;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class XpBarPlugin extends JavaPlugin {

    private final Map<UUID, XpHud> hudByPlayer = new ConcurrentHashMap<>();

    public XpBarPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        var p = "Common/UI/Custom/XpBar/XpHud.ui";
        var url = getClass().getClassLoader().getResource(p);
        getLogger().atInfo().log("[XpBar] Resource check: " + p + " -> " + url);

        getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        getCommandRegistry().registerCommand(new XpSetCommand(this));
    }

    private void onPlayerReady(PlayerReadyEvent event) {

        Ref<EntityStore> entityRef = event.getPlayerRef();
        Store<EntityStore> store = entityRef.getStore();

        Player player = store.getComponent(entityRef, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());

        // Atrasar 1s antes de aplicar o HUD
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            // Se você tiver acesso ao world do player, faça:
            var world = player.getWorld(); // <-- se existir na sua API
            world.execute(() -> {
                XpHud hud = new XpHud(playerRef);
                player.getHudManager().setCustomHud(playerRef, hud);
                hudByPlayer.put(playerRef.getUuid(), hud);
                getLogger().atInfo().log("[XpBar] HUD XP aplicado para " + player.getDisplayName());
            });
        }, 1, TimeUnit.SECONDS);

    }

    public XpHud getHud(UUID uuid) {
        return hudByPlayer.get(uuid);
    }

    @Override
    protected void shutdown() {
        hudByPlayer.clear();
    }
}