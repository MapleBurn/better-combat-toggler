package com.mapleburn.bettercombattoggler;

import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatToggleState {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatToggleState.class);
    private static final Map<UUID, Boolean> SERVER_STATE = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> CLIENT_STATE = new ConcurrentHashMap<>();

    private CombatToggleState() {}

    public static boolean isEnabled(Player player) {
        // Check the logical side (client vs server thread), not the physical distribution
        // player.level().isClientSide() returns false on the server thread (integrated or dedicated)
        boolean isClientSide = player.level().isClientSide();
        boolean result;
        
        if (!isClientSide) {
            // We're on the server logical side - check server state
            result = SERVER_STATE.getOrDefault(player.getUUID(), true);
            LOGGER.debug("isEnabled() called on SERVER side for {}: {} (SERVER_STATE has: {})",
                player.getName().getString(), result, SERVER_STATE.containsKey(player.getUUID()));
        } else {
            // We're on the client logical side - check client state
            result = CLIENT_STATE.getOrDefault(player.getUUID(), true);
            LOGGER.debug("isEnabled() called on CLIENT side for {}: {} (CLIENT_STATE has: {})",
                player.getName().getString(), result, CLIENT_STATE.containsKey(player.getUUID()));
        }
        
        return result;
    }

    public static void setServerState(UUID playerId, boolean enabled) {
        LOGGER.info("Setting SERVER_STATE for {}: {}", playerId, enabled);
        SERVER_STATE.put(playerId, enabled);
    }

    public static void setClientState(UUID playerId, boolean enabled) {
        LOGGER.info("Setting CLIENT_STATE for {}: {}", playerId, enabled);
        CLIENT_STATE.put(playerId, enabled);
    }
}
