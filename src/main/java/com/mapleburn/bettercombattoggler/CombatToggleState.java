package com.mapleburn.bettercombattoggler;

import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatToggleState {
    private static final Map<UUID, Boolean> SERVER_STATE = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> CLIENT_STATE = new ConcurrentHashMap<>();

    private CombatToggleState() {}

    public static boolean isEnabled(Player player) {
        boolean isClientSide = player.level().isClientSide();
        boolean result;
        
        if (!isClientSide) {
            result = SERVER_STATE.getOrDefault(player.getUUID(), true);
        } else {
            result = CLIENT_STATE.getOrDefault(player.getUUID(), true);
        }
        
        return result;
    }

    public static void setServerState(UUID playerId, boolean enabled) {
        SERVER_STATE.put(playerId, enabled);
    }

    public static void setClientState(UUID playerId, boolean enabled) {
        CLIENT_STATE.put(playerId, enabled);
    }
}
