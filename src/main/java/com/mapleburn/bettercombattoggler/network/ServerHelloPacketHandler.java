package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.ModServerStatus;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side handler for the ServerHelloPacket.
 * Updates the ModServerStatus to indicate the server has the mod.
 */
public class ServerHelloPacketHandler {
    
    public static void handlePacket(ServerHelloPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ModServerStatus.setServerHasMod(packet.modPresent()));
    }
}
