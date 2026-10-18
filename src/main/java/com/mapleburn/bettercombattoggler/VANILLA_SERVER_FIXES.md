# Vanilla Server Compatibility Fixes

## Problems Fixed

### 1. **Combat Disabled on Vanilla Server Join**
**Issue**: When a player had the toggle set to OFF in their config and joined a vanilla server (without the mod), the old config state was loaded first, leaving combat disabled and preventing attacks.

**Solution**: 
- `ModServerStatus` now tracks when server status is confirmed via handshake
- When `ServerHelloPacketHandler` receives confirmation that server doesn't have mod:
  - Calls `BetterCombatTogglerClient.resetConfigLoadFlag()`
  - This forces config to reload on the next tick
- On reload, combat is forced to **ENABLED** for vanilla servers
- Config file is NOT updated, preserving player's preference

### 2. **Toggle Crash on Vanilla Servers**
**Issue**: Attempting to toggle combat on vanilla servers caused a crash with:
```
java.lang.UnsupportedOperationException: Payload bettercombattoggler:toggle_combat may not be sent to the server!
```

**Solution**:
- `BetterCombatTogglerClient` now checks if server has mod before allowing toggle
- If server is confirmed to NOT have mod: key press is consumed with error message
- Packet send wrapped in try-catch as additional safety net
- Players see feedback: "Cannot toggle Better Combat on vanilla servers!"

## Technical Changes

### ModServerStatus.java
- Added `statusConfirmed` flag to distinguish between "not yet confirmed" and "confirmed to not have mod"
- Added `isStatusConfirmed()` method

### ServerHelloPacketHandler.java
- Now imports `BetterCombatTogglerClient`
- When server doesn't have mod, calls `resetConfigLoadFlag()`
- This ensures combat state is re-evaluated with the correct rules

### BetterCombatTogglerClient.java

**Config Loading Logic:**
```java
if (!configLoaded && mc.player != null) {
    configLoaded = true;
    CombatTogglerConfig.loadConfig();
    boolean savedState = CombatTogglerConfig.isCombatEnabled();
    
    // Force enabled on vanilla servers (but don't save config)
    if (ModServerStatus.isStatusConfirmed() && !ModServerStatus.serverHasMod()) {
        savedState = true; // Forced, not saved
    }
    
    // Apply state to Better Combat and client
    CombatToggleState.setClientState(...);
    net.bettercombat.client.BetterCombatClientMod.ENABLED = savedState;
}
```

**Toggle Prevention:**
```java
if (ModServerStatus.isStatusConfirmed() && !ModServerStatus.serverHasMod()) {
    if (TOGGLE_KEY.consumeClick()) {
        mc.player.displayClientMessage(
            Component.literal("§cCannot toggle Better Combat on vanilla servers!"), true
        );
    }
    return; // Skip toggle logic entirely
}
```

**Packet Send Safety:**
```java
try {
    PacketDistributor.sendToServer(new ToggleCombatPacket(...));
} catch (Exception e) {
    LOGGER.error("Failed to send packet to server", e);
    mc.player.displayClientMessage(
        Component.literal("§cFailed to send toggle to server!"), true
    );
    continue; // Don't update state if packet send fails
}
```

**Reset Method:**
```java
public static void resetConfigLoadFlag() {
    LOGGER.info("Resetting config load flag...");
    configLoaded = false;
}
```

## Behavior After Fixes

### Vanilla Server (No Mod)
✅ Combat always enabled (forced)
✅ Toggle keybind is disabled with user feedback
✅ No crashes
✅ Config file preserved for when they return to mod servers

### Mod Server (Has Mod)
✅ Full functionality (unchanged)
✅ Toggle works normally
✅ Config syncs with server

### Singleplayer
✅ Full functionality (unchanged)
✅ Toggle works normally
✅ Config syncs as before

## Testing Steps

1. **With vanilla server config OFF**:
   - Join vanilla server → Combat should be enabled automatically
   - Try pressing toggle key → Should show error message
   - Attack should work

2. **With vanilla server config ON**:
   - Join vanilla server → Combat should stay enabled
   - Try pressing toggle key → Should show error message

3. **Return to mod server**:
   - Config should still have OFF state
   - Toggle should work normally
   - Can disable combat again if desired

4. **Singleplayer**:
   - All features work as before
   - Config persists correctly
