package com.mapleburn.bettercombattoggler.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from server to client to confirm the mod is installed on the server.
 */
public record ServerHelloPacket(boolean modPresent) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerHelloPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("bettercombattoggler", "server_hello"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerHelloPacket> STREAM_CODEC =
            StreamCodec.of(ServerHelloPacket::encode, ServerHelloPacket::decode);

    public static void encode(RegistryFriendlyByteBuf buf, ServerHelloPacket packet) {
        buf.writeBoolean(packet.modPresent);
    }

    public static ServerHelloPacket decode(RegistryFriendlyByteBuf buf) {
        return new ServerHelloPacket(buf.readBoolean());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
