package com.example.xpbar;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class XpService {

    public static final int MAX_LEVEL = 100;
    public static final int LEVELS_PER_HIT = 10;

    private final Map<UUID, Integer> levelByPlayer = new ConcurrentHashMap<>();

    // Você provavelmente já tem esse map no seu plugin (UUID -> XpHud).
    // Aqui deixei como interface pra não te amarrar em uma classe específica.
    private final HudBridge hudBridge;

    public interface HudBridge {
        void setHudLevel(PlayerRef playerRef, int level);
    }

    public XpService(HudBridge hudBridge) {
        this.hudBridge = hudBridge;
    }

    public int getLevel(PlayerRef playerRef) {
        return levelByPlayer.getOrDefault(playerRef.getUuid(), 0);
    }

    public int addLevels(PlayerRef playerRef, int delta) {
        UUID id = playerRef.getUuid();
        int current = levelByPlayer.getOrDefault(id, 0);
        int next = Math.min(MAX_LEVEL, current + delta);
        levelByPlayer.put(id, next);

        // Atualiza HUD (se você já criou e anexou o HUD no PlayerReady)
        hudBridge.setHudLevel(playerRef, next);

        return next;
    }
}