package com.mapleburn.bettercombattoggler.mixin;

import com.mapleburn.bettercombattoggler.CombatToggleState;
import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAttackHelper.class)
public abstract class PlayerAttackHelperMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("BetterCombatToggler-Mixin");

    @Inject(method = "getCurrentAttack", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bettercombattoggler$returnNullWhenDisabled(
            Player player,
            int comboCount,
            CallbackInfoReturnable<AttackHand> cir
    ) {
        boolean enabled = CombatToggleState.isEnabled(player);
        LOGGER.info("=== PlayerAttackHelper.getCurrentAttack called ===");
        LOGGER.info("Player: {}, Combat enabled: {}, Will return null: {}", 
            player.getName().getString(), enabled, !enabled);
        
        if (!enabled) {
            LOGGER.info("Blocking Better Combat attack - returning null");
            cir.setReturnValue(null);
        }
    }
}