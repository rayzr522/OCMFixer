# OCMFixer

Fixes the attribute values of all offline players by reading and modifying all playerdata files in a world folder.

- [Installation](#installation)
- [Commands](#commands)
- [Tutorial](#tutorial)
- [Join Me](#join-me)

## Installation

You can [download the latest version from the releases page](https://www.github.com/Rayzr522/OCMFixer/releases).

## Commands

### `/read`

Usage: `/read <world name>`  
Permission: None  
Description: Reads the playerdata files in the given world folder and resets them all.

_Warning: There is no permission for `/read`, so you'll want to uninstall it after you're done using it._

## Tutorial

_Warning: To apply this to all players, it's suggested to do this while no players are online. The changes made by this plugin will be overwritten for any player that is online while executing the command, meaning it won't work for them. It's probably best to do this immediatly after a restart, or after doing `/kickall`._

To know which world to specify, look through all your worlds for one that has a non-empty `playerdata` folder. Then, to fix the world, just do `/read <world>` from the console (_or in-game, but again, it's best not to do this while anyone is online, including yourself_). Usually the world that has the playerdata files is the one specified as the main world in your `server.properties`. As an example, for my test server the main world was `Hub`. To fix it I just did `/read Hub`. It's as easy as that!
