package com.mapleburn.bettercombattoggler;

import com.mapleburn.bettercombattoggler.config.CombatTogglerConfig;
import com.mapleburn.bettercombattoggler.network.ServerHelloPacket;
import com.mapleburn.bettercombattoggler.network.ServerHelloPacketHandler;
import com.mapleburn.bettercombattoggler.network.ToggleCombatPacket;
import com.mapleburn.bettercombattoggler.network.ToggleCombatPacketHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(BetterCombatToggler.MOD_ID)
public class BetterCombatToggler {
    public static final String MOD_ID = "bettercombattoggler";

    public BetterCombatToggler(IEventBus modEventBus) {
        modEventBus.addListener(this::registerPayloads);
        
        // Register server-side event to send hello packet when player joins
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BetterCombatTogglerClient.init(modEventBus);
        }
    }
    
    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Load config and initialize server-side state when player joins
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            CombatTogglerConfig.loadConfig();
            boolean configState = CombatTogglerConfig.isCombatEnabled();

            CombatToggleState.setServerState(serverPlayer.getUUID(), configState);
            PacketDistributor.sendToPlayer(serverPlayer, new ServerHelloPacket(true));
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1")
            .optional(); // Makes clients be able to join even when mod isn't on server

        registrar.playToServer(ToggleCombatPacket.TYPE, ToggleCombatPacket.STREAM_CODEC, ToggleCombatPacketHandler::handlePacket);
        registrar.playToClient(ServerHelloPacket.TYPE, ServerHelloPacket.STREAM_CODEC, ServerHelloPacketHandler::handlePacket);
    }
}
