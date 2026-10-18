package com.mapleburn.bettercombattoggler.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to confirm the mod is installed on the server.
 */
public record ServerHelloPacket(boolean modPresent) {

    public static void encode(ServerHelloPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.modPresent);
    }

    public static ServerHelloPacket decode(FriendlyByteBuf buf) {
        return new ServerHelloPacket(buf.readBoolean());
    }
}
