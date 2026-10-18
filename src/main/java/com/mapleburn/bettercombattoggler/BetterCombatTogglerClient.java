package com.mapleburn.bettercombattoggler;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class BetterCombatTogglerClient {

    public static KeyMapping TOGGLE_KEY;

    public static void init() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(BetterCombatTogglerClient::onRegisterKeys);
        MinecraftForge.EVENT_BUS.addListener(BetterCombatTogglerClient::onClientTick);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        TOGGLE_KEY = new KeyMapping(
                "key.bettercombattoggler.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.bettercombattoggler"
        );
        event.register(TOGGLE_KEY);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || TOGGLE_KEY == null) return;

        while (TOGGLE_KEY.consumeClick()) {
            Boolean enabled = toggleBetterCombat();

            if (enabled == null) {
                mc.player.displayClientMessage(
                        Component.literal("§cBetter Combat not found or incompatible."),
                        true
                );
                continue;
            }

            String status = enabled ? "§aEnabled" : "§cDisabled";
            mc.player.displayClientMessage(
                    Component.literal("§6Better Combat: " + status),
                    true
            );
        }
    }

    private static Boolean toggleBetterCombat() {
        try {
            Class<?> clazz = Class.forName("net.bettercombat.client.BetterCombatClient");
            Field enabledField = clazz.getDeclaredField("ENABLED");
            enabledField.setAccessible(true);

            boolean current = enabledField.getBoolean(null);
            boolean updated = !current;
            enabledField.setBoolean(null, updated);

            return updated;
        } catch (Exception e) {
            return null;
        }
    }
}