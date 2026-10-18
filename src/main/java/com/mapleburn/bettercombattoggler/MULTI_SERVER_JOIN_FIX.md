# Multi-Server Join Bug Fix

## Problem
When a player with combat toggled OFF joined a vanilla server, left, and joined again (without restarting the game), they couldn't attack until manually toggling with the keybind.

## Root Cause
The config load flag (`configLoaded`) was only checked on first join. On subsequent joins:
1. Config loaded with saved state (OFF)
2. ServerHello hadn't arrived yet, so `statusConfirmed=false`
3. The vanilla server safety check didn't apply (it required `statusConfirmed=true`)
4. Combat remained OFF, breaking attack functionality
5. Player had to manually toggle to fix it

## Solution

### 1. **PlayerLoggedInEvent Handler** (BetterCombatTogglerClient)
```java
private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    LOGGER.info("Player joined - resetting config load state");
    configLoaded = false;
}
```
Ensures config is reloaded on **every** server join, not just the first time.

### 2. **Safety Precaution Logic** (BetterCombatTogglerClient)
```java
// Safety check: if server status hasn't been confirmed yet, default to enabled
// to prevent players from being unable to attack on vanilla servers
else if (!ModServerStatus.isStatusConfirmed()) {
    LOGGER.info("Server status not yet confirmed - defaulting to ENABLED as precaution");
    savedState = true;
}
```
If ServerHello hasn't arrived yet, **assume vanilla server** and enable combat. This prevents the "can't attack" bug.

### 3. **ServerHelloPacketHandler Always Resets Flag**
```java
// Always reset the config load flag when we confirm server status
// This ensures we reload config with the correct logic:
// - If vanilla server: force enabled
// - If mod server: load actual saved state
BetterCombatTogglerClient.resetConfigLoadFlag();
```
Previously only reset for vanilla servers. Now resets for both:
- **Vanilla server**: Forces enabled on reload (safety)
- **Mod server**: Loads actual saved config on reload (correctness)

## Flow Diagrams

### First Join - Vanilla Server (config=OFF)
```
Join → onPlayerLogin(configLoaded=false)
   ↓
onClientTick: load config (OFF)
statusConfirmed=false → default to true (ENABLED)
configLoaded=true
   ↓
ServerHello arrives (modPresent=false)
→ resetConfigLoadFlag()
   ↓
Next tick: load config (OFF)
statusConfirmed=true, serverHasMod=false
→ force to true (ENABLED)
   ↓
✅ Combat Enabled
```

### Second Join - Vanilla Server (config=OFF)
```
Leave → onPlayerLogout(configLoaded=false)
   ↓
Join → onPlayerLogin(configLoaded=false)
   ↓
onClientTick: load config (OFF)
statusConfirmed=false → default to true (ENABLED)
configLoaded=true
   ↓
ServerHello arrives (modPresent=false)
→ resetConfigLoadFlag()
   ↓
Next tick: load config (OFF)
statusConfirmed=true, serverHasMod=false
→ force to true (ENABLED)
   ↓
✅ Combat Enabled - NO MANUAL TOGGLE NEEDED
```

### Join Mod Server (config=OFF)
```
Join → onPlayerLogin(configLoaded=false)
   ↓
onClientTick: load config (OFF)
statusConfirmed=false → default to true (as safety)
configLoaded=true
   ↓
ServerHello arrives (modPresent=true)
→ resetConfigLoadFlag()
   ↓
Next tick: load config (OFF)
statusConfirmed=true, serverHasMod=true
→ neither condition applies
→ apply saved state (OFF)
   ↓
✅ Combat Disabled - Config Preserved
```

## Key Improvements

✅ **Multi-join safety**: Config reloads on each join
✅ **Timing resilience**: Defaults to enabled before ServerHello arrives
✅ **Server detection**: Correctly applies logic once confirmed
✅ **Config preservation**: Mod servers load actual saved state
✅ **No manual intervention needed**: Combat works correctly on all joins

## Testing Checklist

- [ ] Join vanilla server (config OFF) → Combat enabled
- [ ] Leave, rejoin vanilla server → Combat still enabled, no toggle needed
- [ ] Multiple vanilla server joins → Always enabled
- [ ] Join mod server (config OFF) → Combat disabled as configured
- [ ] Switch between vanilla and mod servers → Correct behavior each time
- [ ] Singleplayer → Normal toggle functionality works
