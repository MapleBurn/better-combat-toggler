package com.mapleburn.bettercombattoggler.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public record ToggleCombatPacket(UUID playerId, boolean enabled) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleCombatPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("bettercombattoggler", "toggle_combat"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleCombatPacket> STREAM_CODEC =
            StreamCodec.of(ToggleCombatPacket::encode, ToggleCombatPacket::decode);

    public static void encode(RegistryFriendlyByteBuf buf, ToggleCombatPacket packet) {
        buf.writeUUID(packet.playerId);
        buf.writeBoolean(packet.enabled);
    }

    public static ToggleCombatPacket decode(RegistryFriendlyByteBuf buf) {
        return new ToggleCombatPacket(buf.readUUID(), buf.readBoolean());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
