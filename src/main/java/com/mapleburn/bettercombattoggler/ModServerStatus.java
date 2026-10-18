package com.mapleburn.bettercombattoggler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks whether the server has this mod installed.
 * This allows the client to gracefully degrade when connecting to vanilla servers.
 */
public class ModServerStatus {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModServerStatus.class);
    private static boolean serverHasMod = false;
    private static boolean statusConfirmed = false;
    
    private ModServerStatus() {}
    
    /**
     * Sets whether the server has this mod installed.
     * This should be called when the payload handler confirms communication.
     */
    public static void setServerHasMod(boolean hasMod) {
        LOGGER.info("Server mod status confirmed: {}", hasMod ? "present" : "absent");
        serverHasMod = hasMod;
        statusConfirmed = true;
    }
    
    /**
     * Checks if the server has this mod installed.
     * Returns true if in singleplayer or if server confirmed to have the mod.
     */
    public static boolean serverHasMod() {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();
        
        // In singleplayer, we always have the mod
        if (connection != null && mc.hasSingleplayerServer()) {
            return true;
        }
        
        return serverHasMod;
    }
    
    /**
     * Checks if the server status has been confirmed via handshake.
     */
    public static boolean isStatusConfirmed() {
        return statusConfirmed;
    }
    
    /**
     * Resets the server status when disconnecting.
     */
    public static void reset() {
        LOGGER.info("Resetting server mod status");
        serverHasMod = false;
        statusConfirmed = false;
    }
    
    /**
     * Checks if we should send packets to the server.
     * Only send if server has the mod or we're in singleplayer.
     */
    public static boolean canSendPackets() {
        boolean canSend = serverHasMod();
        if (!canSend) {
            LOGGER.debug("Cannot send packets - server doesn't have mod installed");
        }
        return canSend;
    }
}
