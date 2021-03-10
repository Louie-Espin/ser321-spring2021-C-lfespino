# Assignment 3 lfespino

## How to run

## For TCP
* 	First, use "gradle runServer" for default port 8080
* 	If running this on a second system, use "gradle runServer −P port = port"

*	Next, use "gradle runClient" for default port 8080 and localhost
*	If running server on a second system, use "gradle runClient − P port = port − P host = hostIP"

## Problem 1: TCP sockets
### Requirements Fullfilled

*	When the clients starts up connects to the Server, the server will
	reply by asking for the name of the player.

*	The client should send their name and the server should receive it and
	greet the client by name and ask for the number of questions the client wants to try
	to answer correctly in time.

*	The client should enter a number and the server should use that number and the
	previous client name to tell them they are ready to play.

*	After the user enters the name and num questions the server waits for a
	"start" input which will start the question round.

*	When the server receives a "start" it will start a timer with 5 sec * num
	questions. This is the time the client has to answer "num" questions correctly.

*	The server will then send over the first question with an image of the pokemon.
	The answer is printed in the server commandline.

*	The client must respond with the name of the pokemon. The client itself does not
	know the answers nor store the questions.

*	The client enters an answer and the server checks the answer and responds accordingly.
	The client can try as many times as they would like to give the correct answer.

*	After each question loop, the server checks the current time and compares it to how much
	time is left. If time has run out, the server send out "Time Out!" and a failure image

*	If the server receives enough correct answers (based on num questions)
	and the timer did not run out, then the server will send a "winner" image,

*	The server sends out an image at the start, for each pokemon, and for win/lose conditions

*	Images are only know by and handled on the server.

*	Evaluations of the answer happen on the server side, the client
	does not know the questions and their answers.

### UML Description
	
	UML picture was added in the project folder

## Files

### ClientGui.java
### Client.java
### Server.java

### ClientUDP.java
### ServerUDP.java
