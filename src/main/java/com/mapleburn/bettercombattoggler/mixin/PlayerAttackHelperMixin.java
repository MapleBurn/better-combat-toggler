package com.mapleburn.bettercombattoggler.mixin;

import com.mapleburn.bettercombattoggler.CombatToggleState;
import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAttackHelper.class)
public abstract class PlayerAttackHelperMixin {
    @Inject(method = "getCurrentAttack", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bettercombattoggler$returnNullWhenDisabled(Player player, int comboCount, CallbackInfoReturnable<AttackHand> cir) {
        if (!CombatToggleState.isEnabled(player)) {
            cir.setReturnValue(null);
        }
    }
}
