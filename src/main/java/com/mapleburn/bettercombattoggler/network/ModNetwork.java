package com.mapleburn.bettercombattoggler.network;

import com.mapleburn.bettercombattoggler.BetterCombatToggler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    // acceptMissingOr makes clients be able to join even when mod isn't on server (and vice versa)
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BetterCombatToggler.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION),
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION)
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, ToggleCombatPacket.class, ToggleCombatPacket::encode, ToggleCombatPacket::decode,
                ToggleCombatPacketHandler::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, ServerHelloPacket.class, ServerHelloPacket::encode, ServerHelloPacket::decode,
                ServerHelloPacketHandler::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
