# Hangman
In this course project, you are required to write a network application that implements a
client/server gaming environment using TCP API in Java.
The environment runs hangman (المشنقة حبل (games between clients such that each game
consists of two players. More than one game can be run at the same time.
Attached with this document is a file named hangman.out. The file consists of a serialized
ArrayList of Strings. Each String in the ArrayList is structured as follows:
genre – word
The word represents the word that will be played with, the genre represents the type of this
word (Movie, Actor, Actress, TV Show).
For example:
Actor – Robert De Niro
At the server side, when the server application is first started, the ArrayList of Strings must
be read from the file hangman.out. The server application must then start taking each two
clients’ connections and start a game for them as follows:
1. When a player is connected to the server, he should enter his name (from the client
side) which will represent him in the game.
2. If a client (player) connects to the server and there is no other player to start a game
with, the server waits for another client to connect to start the game. The connected
client must be notified that the server is waiting for another player to connect. The
client application must display the message “Waiting for another player to connect”
accordingly. Once another player connects, both players must be notified that the
game has started and the statement “Game has started” must be displayed through
the client application. This means that if a player connects, and there is already a
player connected waiting, he will be notified directly that the game has started.
3. When the game starts, the server application must select a String randomly from the
read ArrayList of Strings. The genre of the word must be displayed to the players
(through the client application) in addition to the word syllabi with letters replaced
by _. The client application must also display the list of incorrect letters that have
been entered by both player, this list is initially empty.
For example, if the word is Robert De Niro, the following must be displayed to both
players:
Genre: Actor
_ _ _ _ _ _ _ _ _ _ _ _
Incorrect letters:
4. The server application must randomly select one of the players to start.
5. When it’s a player turn, the client application must display to him that it’s his turn.
For the other player, it must be displayed that it’s “first player name” turn. The player
whose turn to play must be allowed to enter a letter or make a guess. If he chooses to
enter a letter, it must read a letter and checks whether it exists in the word, it must
update the word displayed to be both players with all letters’ existences. If the letter
does not exist it must be added to the list of incorrect letters displayed to both players.
For example, in the previous word, if the player enters the letter r, the following must
be displayed to both players:
Genre: Actor
R _ _ _ r _ _ _ _ _ r _
Incorrect letters:
If the other player next enters the letter a, the following must be displayed to both
players:
Genre: Actor
R _ _ _ r _ _ _ _ _ r _
Incorrect letters: a
If the player chooses to make a guess, the client application must read his guess, if
it’s correct it must display the word to both players, write to the winner the statement
“YOU WIN” and to the other player “other player’s name WIN”.
If the player chooses to make a guess, he is not allowed to go back and enter a letter.
If he chooses to enter a letter, before returning to the other player, the game must ask
him if he wants to make a guess.
6. The process in step 5 must keep on running, alternating turns between player until
one of them makes a correct guess and wins the game.
7. The server application must be able to run more than one game at the same time and
must keep on taking clients connections and starting games between players. 
