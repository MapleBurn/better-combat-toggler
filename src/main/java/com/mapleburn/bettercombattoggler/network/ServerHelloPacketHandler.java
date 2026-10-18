package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.ModServerStatus;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-side handler for the ServerHelloPacket.
 * Updates the ModServerStatus to indicate the server has the mod.
 */
public class ServerHelloPacketHandler {

    public static void handlePacket(ServerHelloPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ModServerStatus.setServerHasMod(packet.modPresent()));
        context.setPacketHandled(true);
    }
}
