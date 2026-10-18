package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.CombatToggleState;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToggleCombatPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToggleCombatPacketHandler.class);
    
    public static void handlePacket(ToggleCombatPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // This handler runs on the server side (packet is registered as playToServer)
            // Update the server-side combat state for this player
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) {
                LOGGER.warn("Received packet but player is null!");
                return;
            }
            
            // Update server state so the mixin can check it
            CombatToggleState.setServerState(packet.playerId(), packet.enabled());
        });
    }
}
