package com.mapleburn.bettercombattoggler;

import com.mapleburn.bettercombattoggler.config.CombatTogglerConfig;
import com.mapleburn.bettercombattoggler.network.ModNetwork;
import com.mapleburn.bettercombattoggler.network.ServerHelloPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;

@Mod(BetterCombatToggler.MOD_ID)
public class BetterCombatToggler {
    public static final String MOD_ID = "bettercombattoggler";

    public BetterCombatToggler() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Register server-side event to send hello packet when player joins
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogin);

        if (FMLEnvironment.dist.isClient()) {
            BetterCombatTogglerClient.init(modEventBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Load config and initialize server-side state when player joins
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CombatTogglerConfig.loadConfig();
            boolean configState = CombatTogglerConfig.isCombatEnabled();

            CombatToggleState.setServerState(serverPlayer.getUUID(), configState);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ServerHelloPacket(true));
        }
    }
}
