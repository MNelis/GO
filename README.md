# GO
version 1.3

### Start the server
- Open command prompt at the location of the `GO-v1.3.jar`-file.
- Enter `java -cp GO-v1.3.jar server.model.GOServer <port number>`
- If no port number is entered, the default port is used: 5647.

### Start a client
For each new client:
- Open command prompt at the location of the `GO-v1.3.jar`-file.
- Enter `java -cp GO-v1.3.jar client.model.GOClient <name> <host address> <port number>`
- If no port number is entered, the default port is used: 5647.

#### Using a client
There are several commands a client can make depending on its status. The user interface will guide the client such that it can use the application without consulting this file.

When a client enters the **lobby**, a menu is shown with the following commands:
- `0 <your message>` to send a message to all the other clients in the lobby.
- `1` to request a game.
- `2` to activate/deactivate the computer player.
- `3` to disconnect and close the client application.
- `HELP` to show the menu.

At the start of the game, the first client who requested a game must determine the settings. The client must choose its own color (black or white) and determine the size of the board. The board size it determined by a single integer between 5 and 19.
- `SET <B/W> <board size>` to determine the settings of the game. 
  
When a client starts a **game**, a menu is shown with the following commands:
- `0 <your message>` to send a message to the other client in the game.
- `1` to quit the game at any time and return to the lobby. You will automatically lose the game.
- `2` to activate/deactivate the computer player.
- `3` to disconnect and close the client application.
- `HELP` to show the menu.

Furthermore, moves are made with the following commands.
- `MOVE <row> <column>` to place a stone on (row,column).
- `MOVE PASS` to pass your turn.
