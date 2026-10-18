package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.CombatToggleState;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToggleCombatPacketHandler {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ToggleCombatPacketHandler.class);
    
    public static void handlePacket(ToggleCombatPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) {
                //LOGGER.warn("Received packet but player is null!");
                return;
            }

            CombatToggleState.setServerState(packet.playerId(), packet.enabled());
        });
    }
}
