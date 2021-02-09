# Minegage-Core
Clone of Core framework for Minegage

### Structure
This project is grouped into several libraries, which can be attached as necessary to create new libraries depending on the required scope.

The most basic library is Common, which includes the basic [PluginModule](https://github.com/AGuyWhoSkis/Minegage/blob/main/Common/src/net/minegage/common/module/PluginModule.java) class. Plugin Modules are a helper class which make coding each section of a library easier.
Common includes numerous helpful Plugin Modules. For example, [Ticker.java](https://github.com/AGuyWhoSkis/Minegage/blob/main/Common/src/net/minegage/common/ticker/Ticker.java) allows developers to easily create code which executes every tick, second, minute, etc.

The Core library includes code shared by all plugins running in production. It includes modules that manage user permissions, commands shared by all servers, chat filters, and several interfaces that simplify the native server API.

Each of the following libraries are built into .jar files which run in production:
- Minigame: Runs playable minigames. Includes lobbies with map voting and kit selection, and nine minigames. Most importantly are the files in [/games/](https://github.com/AGuyWhoSkis/Minegage/tree/main/Minigame/src/net/minegage/minigame/game), which allow the creation of uniquely behaving minigames in just a few lines of code. For an example of this, see [GamePaintball.java](https://github.com/AGuyWhoSkis/Minegage/blob/main/Minigame/src/net/minegage/minigame/game/games/paintball/GamePaintball.java). Because it implements [GameTDM](https://github.com/AGuyWhoSkis/Minegage/blob/main/Minigame/src/net/minegage/minigame/game/GameTDM.java), most of the coding is already done; all that is unique to this game is the items players receive, and a few cancelled events unique to this minigame.

- Hub: The main lobby, which players log into upon connecting to the server. Includes purchaseable cosmetics, server selection for players, and most features of the Core and Common libraries.

- Build: The server for builders to make maps for use in minigames. Allows players to set map metadata like spawn points, kit items, etc.

The following libraries are smaller implementations that only use the Common library:
- Recording: Made for Youtubers to let them set a recording status above their name with the [CommonBoardManager](https://github.com/AGuyWhoSkis/Minegage/blob/main/Common/src/net/minegage/common/board/CommonBoardManager.java) class

- Creative, Skyblock, SOTF, Factions, Skyblock: Includes only basic commands, chat management, and permission management. The rest of the behaviour of these servers is defined by other plugins.
