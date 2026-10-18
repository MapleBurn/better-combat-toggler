package com.mapleburn.bettercombattoggler.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.UUID;

public record ToggleCombatPacket(UUID playerId, boolean enabled) {

    public static void encode(ToggleCombatPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerId);
        buf.writeBoolean(packet.enabled);
    }

    public static ToggleCombatPacket decode(FriendlyByteBuf buf) {
        return new ToggleCombatPacket(buf.readUUID(), buf.readBoolean());
    }
}
