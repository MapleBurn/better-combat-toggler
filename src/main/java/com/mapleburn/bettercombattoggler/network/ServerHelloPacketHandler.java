package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.ModServerStatus;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side handler for the ServerHelloPacket.
 * Updates the ModServerStatus to indicate the server has the mod.
 */
public class ServerHelloPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHelloPacketHandler.class);
    
    public static void handlePacket(ServerHelloPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            LOGGER.info("=== SERVER HELLO RECEIVED ===");
            LOGGER.info("Server has mod: {}", packet.modPresent());
            
            // Update the server status so the client knows it can send packets
            ModServerStatus.setServerHasMod(packet.modPresent());
        });
    }
}
