# Server-Optional Compatibility Changes

## Overview
This mod has been updated to be **server-side optional**, meaning players can now join servers that don't have the mod installed, while still maintaining full functionality when both client and server have it.

## How It Works

### When Server Has the Mod
- Full functionality: Client and server communicate via packets
- Server tracks combat state for proper mixin behavior
- State is synchronized between client and server

### When Server Doesn't Have the Mod
- Client-only mode: All functionality works locally
- Better Combat toggling still functions (client-side)
- No packets are sent to the server (prevents errors)
- Mixins still work for client-side combat behavior

## Technical Changes

### 1. **ModServerStatus.java** (New File)
- Tracks whether the server has the mod installed
- Provides `canSendPackets()` method to check before sending
- Automatically detects singleplayer (always has mod)
- Resets when disconnecting from server

### 2. **ServerHelloPacket.java** (New File)
- Packet sent from server to client on login
- Confirms to the client that server has the mod
- Allows client to enable full functionality

### 3. **ServerHelloPacketHandler.java** (New File)
- Handles the server hello packet on client side
- Updates ModServerStatus when received

### 4. **BetterCombatToggler.java** (Modified)
- Marked payload registrar as `.optional()`
- Registered bidirectional packet communication
- Added PlayerLoggedInEvent handler to send hello packet
- Server now announces its presence to clients

### 5. **BetterCombatTogglerClient.java** (Modified)
- Wraps all `PacketDistributor.sendToServer()` calls with `ModServerStatus.canSendPackets()`
- Only sends packets when server has the mod
- Resets server status on logout
- Logs when operating in client-only mode

## Benefits

✅ **Maximum Compatibility**: Players can join any server, with or without the mod
✅ **No Errors**: Client won't send packets that would be rejected
✅ **Graceful Degradation**: Mod still works client-side when server doesn't have it
✅ **Full Functionality**: When both have it, everything works as before
✅ **Better Combat Integration**: Mixin still toggles Better Combat mod on client

## Testing Recommendations

1. **Singleplayer**: Should work exactly as before
2. **Multiplayer with mod on server**: Should work exactly as before
3. **Multiplayer without mod on server**: Should work in client-only mode without errors

## Notes

- The mod primarily affects client-side combat behavior (Better Combat toggle)
- Server-side tracking was mainly for consistency, not critical functionality
- Client can fully function independently when needed
- The mixin targets Better Combat client classes, which work regardless of server
