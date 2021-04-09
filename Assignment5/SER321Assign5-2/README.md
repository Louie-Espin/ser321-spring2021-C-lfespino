## VIDEO LINK
https://youtu.be/dEseTtvwpN0

## REQUIREMENTS MET
- 1 - can send messages to chat and have the other peers receive them.
- 2 - can check if a pawn disonnects and removes then from their peer list.
- 3 - all peers are entered once into the peer list.
- 4 - by calling "joke" you can submit a joke for review. The other peers can vote "yay" or "nay" for your joke.
- 4 - there is an "addJoke" json that then adds the joke to the joke list, and peers can see the list with "jokes".
- 5a - the peers can detect if the leader is disconnected and can keep running as normal.
- 5b - if a leader disconnects, the joke list can still be handled.
- 5c - the peers keep running even if a leader disconnects
- 5d - new peers can join even after the leader disconnects
- 6 - overall good error handling

## HOW TO USE
- type anything to send a message
- type quit to exit the network
- type joke to submit a joke for review
- type jokes to see  the jokes list
- type yay to approve a joke for submission
- type nay to prevent a joke from being submitted to the jokes list
- type peers to see the list of peers you have

## PROTOCOL
I created various JSON strings in order for each peer to communicate
- JOIN: this is sent when a peer wants to joint the network
- REMOVE: this is sent when a peer leaves the network, alerting the other peers to update their list
- JOKE: this is used to submit a joke for review
- YES: this is used to show that the peer has approved the submission of a joke
- NO: this is used to show that the peer has not approved the submission of a joke, preventing it from being added to the joke list
- addJoke: this adds the joke the peers list

## Purpose:
Very basic peer-2-peer for a chat. All peers can communicate with each other. 

Each peer is client and server at the same time. 
When started the peer has a ServerThread in which the peer listens for potential other peers to connect.

You want to first start the leader who is the one in charge of the network

### Running the leader
This will start the leader on a default port and use localhost
	gradle runPeer -PisLeader=true -q --console=plain

If you want to change the leader settings
	gradle runPeer -PpeerName=Hans -Ppeer="localhost:8080" -Pleader="localhost:8080" -PisLeader=true -q --console=plain

You can of course replace localhost with the IP of your AWS, Pi etc. 

### Running a Pawn

So just a peer who is not the leader, minimal with the "default" leader from above
	gradle runPeer -PpeerName=Anna -Ppeer="localhost:9000" -q --console=plain

If you want to change settings
	gradle runPeer -PpeerName=Elsa -Ppeer="localhost:9000" -Pleader="localhost:8080" -q --console=plain

- isLeader is default false so you do not need to set it
- leader: needs to be the same in ALL started peers no matter if leader or pawn

You can start as many pawns (non leaders) as you like they should all connect. 

Watch the video for some more details about the code. 
This code is a basic code that does not include a lot of error handling yet and might need adjustments depending on how you implement your leader election. You can change this code any way you like. 
Some things that it does not do:
- check inputs from Gradle (would be good to include that)
- most requests to the server are not acknowledged, e.g. when a message is send we just send it and the server will never respond to us that they actually go it

