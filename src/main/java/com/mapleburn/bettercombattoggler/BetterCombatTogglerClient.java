package com.mapleburn.bettercombattoggler;

import com.mapleburn.bettercombattoggler.config.CombatTogglerConfig;
import com.mapleburn.bettercombattoggler.network.ToggleCombatPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterCombatTogglerClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BetterCombatTogglerClient.class);
    public static KeyMapping TOGGLE_KEY;
    private static boolean configLoaded = false;

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(BetterCombatTogglerClient::onRegisterKeys);
        NeoForge.EVENT_BUS.addListener(BetterCombatTogglerClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(BetterCombatTogglerClient::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(BetterCombatTogglerClient::onPlayerLogin);
    }
    
    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        configLoaded = false;
    }
    
    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        configLoaded = false;
        ModServerStatus.reset();
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        TOGGLE_KEY = new KeyMapping("key.bettercombattoggler.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.bettercombattoggler");
        event.register(TOGGLE_KEY);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || TOGGLE_KEY == null) return;

        // Load config once when player joins
        if (!configLoaded && mc.player != null) {
            configLoaded = true;
            
            // Load the saved config state from file
            CombatTogglerConfig.loadConfig();
            boolean configState = CombatTogglerConfig.isCombatEnabled();

            LOGGER.info("Loading Better Combat state from config: {}", configState);

            CombatToggleState.setClientState(mc.player.getUUID(), configState);
            net.bettercombat.client.BetterCombatClientMod.ENABLED = configState;
            
            // Set sweep particles appropriately (enabled for vanilla mode, disabled for BC mode)
            net.bettercombat.client.BetterCombatClientMod.config.isSweepingParticleEnabled = !configState;
            
            // Sync config state with server (only if server has the mod)
            if (ModServerStatus.canSendPackets()) {
                LOGGER.info("Sending sync packet to server with state: {}", configState);
                PacketDistributor.sendToServer(new ToggleCombatPacket(mc.player.getUUID(), configState));
            } else {
                LOGGER.info("Server-side mod not detected, skipping packet send");
            }
        }

        while (TOGGLE_KEY.consumeClick()) {
            boolean currentState = CombatToggleState.isEnabled(mc.player);
            boolean newState = !currentState;

            LOGGER.debug("Toggle pressed: Better Combat enabled: {}", newState);

            if (ModServerStatus.canSendPackets()) {
                try {
                    PacketDistributor.sendToServer(new ToggleCombatPacket(mc.player.getUUID(), newState));
                } catch (Exception e) {
                    LOGGER.error("Failed to send packet to server", e);
                    mc.player.displayClientMessage(
                        Component.literal("§cFailed to send toggle to server!"), true
                    );
                    continue;
                }
            }

            // Update local state immediately for responsive UI
            CombatToggleState.setClientState(mc.player.getUUID(), newState);
            CombatTogglerConfig.setCombatEnabled(newState);
            net.bettercombat.client.BetterCombatClientMod.ENABLED = newState;
            
            // Enable sweep particles when vanilla combat is active
            net.bettercombat.client.BetterCombatClientMod.config.isSweepingParticleEnabled = !newState;

            // Show status with mode indicator
            String status = newState ? "§aEnabled" : "§cDisabled";
            String mode = ModServerStatus.canSendPackets() ? "" : " §7(Client-Only)";
            mc.player.displayClientMessage(Component.literal("§6Better Combat: " + status + mode), true);
        }
    }
}
