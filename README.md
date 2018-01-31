# GO

:black_circle::white_circle:

### Start the server
- Open commandprompt in the folder target.
- Enter `java -cp go-1.0-SNAPSHOT.jar server.model.GOServer`

### Start a client
For each new client:
- Open commandprompt in the folder target.
- Enter `java -cp go-1.0-SNAPSHOT.jar client.model.GOClient <name> <hostaddress>`

#### Using a client
There are several commands a client can make depending on its status. The user interface will guide the client such that it can use the application without consulting this file.

A client can **always** use:
- `EXIT` to disconnent and close the client application.


When a client is **in the lobby**, it can use the following commands:
- `REQUESTGAME` to request a game.
- `STARTAI` to activate the computer player. 
- `ENDAI` to deactivate the computer player.
- `CHAT <your message>` to send a message to all the other clients in the lobby.


At the start of the game, the first client who requested a game must determine the settings. The client must choose its own color (black or white) and determine the size of the board. The board size it determined by a single integer between 5 and 19.
- `SETTINGS` <BLACK/WHITE> <boardsize> to determine the settings of the game. 
  
  
When a client is **playing a game**, it can use the following commands:
- `MOVE <row> <column>` to place a stone on (row,column).
- `MOVE PASS` to pass your turn.
- `STARTAI` to activate the computer player. 
- `ENDAI` to deactivate the computer player.
- `QUIT` to quit the game at any time and return to the lobby. You will automatically lose the game.
- `CHAT <your message>` to send a message to the other client in the game.
