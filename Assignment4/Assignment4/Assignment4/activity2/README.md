# Assignment 4 Activity 2
## Description
This code runs client and server tasks which communicate using the Protobuf protocol. It also features:
- A multithreaded server that can run indefinitely
- Clients which can join eachother's games
- A game with randomized Tasks and Images
- A LeaderBoard system saved as a JSON file

## How to run the program
### Terminal
Please use the following commands:
```
    For Server, run "gradle runServer -Pport=9099 -q --console=plain"
    Alternatively: "gradle runServer"
```
```   
    For Client, run "gradle runClient -Phost=localhost -Pport=9099"
    Alternatively: "gradle runClient"
```
## Program Tutorial
### Server
- Build and run the Server with the given gradle command
- Wait for client connections
- You can view the answer to the task question in the command line

### Client
- Build and run the Client with the given gradle command
- Once your server is up and running, you may connect to it by typing your name.
- Main Menu Option "1": Displays a leaderboard
- Main Menu Option "2": New game. Win by revealing the pokemon image when you correctly answer the given tasks
- Main Menu Option "3": Quits the client program

## REQUIREMENTS MET:
1. The project runs through gradle.
2. The protocol was implemented as seen in the Protobuf files.
3. The client program asks for a name, connects to the server, then displays the main menu with three options.
4. When the user types 1: The server sends a LEADER response, and the client displays the leaderboard.
5. The leader board is the same for all clients and is stored as a JSON file.
6. When the user types 2: The server creates a new game and sends a TASK response with an image and question.
7. Multiple clients can enter the same game.
8. When clients finish the image, the server sends a WON response and the user can go back to the main menu.
9. The tasks are sent and checked on the server side. The client does not know the answers.
10. All of the tasks are small questions that are presented neatly and are fast to answer.
11. When the user types 3: The client disconnects gracefully and the server keeps running.
12. When the client disconnects in any other way, the server keeps running.
13. The tasks are based on pokemon trivia and are randomly selected.
14. Currently working on running my server in AWS.
15. Something creative: The server randomly selects the images from a JSON file containing 890 different ASCII drawings of pokemon.
16. ---
17. Currently working on testing my client.
18. The answer is always printed on the server.

#### Purpose:
Demonstrate simple Client and Server communication using `SocketServer` and `Socket`classes.

Here a simple protocol is defined which uses protobuf. The client reads in a json file and then creates a protobuf object from it to send it to the server. The server reads it and sends back the calculated result. 

The response is also a protobuf but only with a result string. 

To see the proto file see: src/main/proto which is the default location for proto files. 

Gradle is already setup to compile the proto files. 

### The procotol
You will see a response.proto and a request.proto file. You should implement these in your program. 
Some constraints on them:
Request:
- NAME: a name is sent to the server
	- name
	Response: GREETING
			- greeting
- LEADER: client wants to get leader board
	- no further data
	Response: LEADER
			- leader
- NEW: client wants to enter a game
	- no further data
	Response: TASK
			- image
			- task
- ANSWER: client sent an answer to a server task
	- answer
	Response: TASK
			- image
			- task
			- eval
	OR
	Response: WON
			- image
- QUIT: clients wants to quit connection
	- no further data
	Response: BYE
		- message

Response ERROR: anytime there is an error you should send the ERROR response and give an appropriate message. Client should act appropriately
	- message

### How to run it (optional)
The proto file can be compiled using

``gradle generateProto``

This will also be done when building the project. 

You should see the compiled proto file in Java under build/generated/source/proto/main/java/buffers

Now you can run the client and server 

#### Default 
Server is Java
Per default on 9099
runServer

You have one example client in Java using the Protobuf protocol

Clients runs per default on 
host localhost, port 9099
Run Java:
	runClient


#### With parameters:
Java
gradle runClient -Pport=9099 -Phost='localhost'
gradle runServer -Pport=9099


