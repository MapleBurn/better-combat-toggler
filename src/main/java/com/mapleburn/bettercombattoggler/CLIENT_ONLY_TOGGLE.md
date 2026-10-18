# Client-Only Toggle Feature

## Feature
Players can now toggle Better Combat even on vanilla servers (without the mod), but in a **client-only mode** with limitations.

## How It Works

### On Servers WITH the Mod
✅ Full functionality
- Toggle sends packet to server
- Server tracks combat state
- Mixin works on both client and server
- Full synchronization

**Player sees**: `Better Combat: Enabled` or `Better Combat: Disabled`

### On Vanilla Servers (WITHOUT the Mod)
✅ Client-only functionality
- Toggle works locally
- `BetterCombatClientMod.ENABLED` is updated (client-side)
- Config is saved for persistence
- **No packet sent to server** (would cause errors)
- Server doesn't track state (no server-side mixin)

**Player sees**: `Better Combat: Enabled (Client-Only)` or `Better Combat: Disabled (Client-Only)`

## What Works Client-Only

Since `net.bettercombat.client.BetterCombatClientMod.ENABLED` is purely client-side:

✅ **Client-side animations** still toggle
✅ **Client-side visual effects** still toggle  
✅ **Config persistence** still works
✅ **Keybind** works normally
✅ **Better Combat client features** can be toggled

## What Doesn't Work Client-Only

❌ **Server-side combat mechanics** - Server doesn't know about toggle
❌ **Server-side mixin** - Only runs if server has mod
❌ **Server state tracking** - Not available on vanilla servers

## Implementation

### Toggle Logic (BetterCombatTogglerClient.java)
```java
while (TOGGLE_KEY.consumeClick()) {
    boolean newState = !currentState;
    
    // Send packet ONLY if server has mod
    if (ModServerStatus.canSendPackets()) {
        PacketDistributor.sendToServer(new ToggleCombatPacket(...));
        LOGGER.info("Sent toggle packet to server");
    } else {
        LOGGER.info("Server doesn't have mod - using client-only mode");
    }
    
    // ALWAYS update client-side state (works everywhere)
    CombatToggleState.setClientState(mc.player.getUUID(), newState);
    CombatTogglerConfig.setCombatEnabled(newState);
    net.bettercombat.client.BetterCombatClientMod.ENABLED = newState;
    
    // Show status with mode indicator
    String mode = ModServerStatus.canSendPackets() ? "" : " §7(Client-Only)";
    mc.player.displayClientMessage(
        Component.literal("§6Better Combat: " + status + mode), true
    );
}
```

## User Experience

### Scenario 1: Vanilla Server
```
Player joins → Combat forced to ENABLED
Player presses toggle → "Better Combat: Disabled (Client-Only)"
Player presses toggle → "Better Combat: Enabled (Client-Only)"
```

### Scenario 2: Mod Server
```
Player joins → Combat forced to ENABLED
Player presses toggle → "Better Combat: Disabled"
Player presses toggle → "Better Combat: Enabled"
```

### Scenario 3: Singleplayer
```
Player loads world → Combat forced to ENABLED
Player presses toggle → "Better Combat: Disabled"
Config saved → Next time loads as Disabled
```

## Benefits

✅ **Maximum flexibility** - Players can toggle anywhere
✅ **Clear feedback** - "(Client-Only)" indicator shows limitations
✅ **Safe operation** - Never sends packets to vanilla servers
✅ **Config persistence** - Works across sessions
✅ **Better Combat integration** - Client features work on all servers

## Code Changes

### Removed
- Toggle blocking logic for vanilla servers
- Error message when trying to toggle on vanilla servers
- `return` statement that prevented toggle execution

### Added
- Mode indicator in feedback message: `" §7(Client-Only)"`
- Logging to distinguish client-only vs server-synced mode

### Unchanged
- Packet sending is still wrapped in `canSendPackets()` check
- Client state updates happen regardless of server mod status
- Config saving happens on all toggles
- Join behavior (always force enabled)
