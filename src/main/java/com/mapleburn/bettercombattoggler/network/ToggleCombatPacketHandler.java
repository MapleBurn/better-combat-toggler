package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.CombatToggleState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleCombatPacketHandler {

    public static void handlePacket(ToggleCombatPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            CombatToggleState.setServerState(packet.playerId(), packet.enabled());
        });
        context.setPacketHandled(true);
    }
}
