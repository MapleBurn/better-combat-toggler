package com.mapleburn.bettercombattoggler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(BetterCombatToggler.MOD_ID)
public class BetterCombatToggler {
    public static final String MOD_ID = "bettercombattoggler";

    public BetterCombatToggler() {
        if (FMLEnvironment.dist.isClient()) {
            BetterCombatTogglerClient.init();
        }
    }
}