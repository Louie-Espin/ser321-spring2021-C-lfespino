# ASSIGNMENT 6

# PROJECT DESCRIPTION
This assignment is a client-server program that initially provided two services: "Echo" and "Jokes". In addition to this I implemented the services "Calc" and "Story". I also teamed up with Braulio Espinoza to design the Translate Services "UWU" and "Flip".

# HOW TO USE
## Running the Node
- Using "gradle runNode" runs a node with the default arguments:
	-PserviceHost="localhost" -PservicePort=8000

## Running the Client
- Using "gradle runClient" runs a client with the default arguments:
	-PserviceHost="localhost" -PservicePort=8000 -PregistryHost="localhost" -PgrpcPort=9002
- Using "gradle runClient -Phost=localhost -Pport=8000 -Pauto=1" lets the client run with hard-coded data

## User-Interface
When ran, and not using -Pauto=1, the program will display the following menu:

Please choose the service
e - Echo service
j - Joke service
s - Story service
c - Calc service
t - Translate service
x - exit

From here the user can choose the various different services: including "Echo", "Joke", "Story", "Calc", and the new "Translate" service. This service prompts you to type in a sentence and then gives you the option to either "flip" said sentence or "UWU" it. The user can also decide to quit.

# REQUIREMENTS MET
## Task 1
1. Run the service node through "gradle runNode" and client through "gradle runClientJava"
2. Implemented calc and story
3. Good client that doesn't crash and takes user input
4. Run with hard-coded data using "gradle runClient -Phost=host -Pport=port -Pauto=1"
5. Server and Client are both robust

## Task 2
- Service allows at least 2 different requests
- Each request needs at least 1 input
- Response returns different data for different requests

### Protocol Design Description
The Translate Service first asks the user for a sentence, then asks to choose between "uwu-ify" and "flip-ify".

"Uwu-ify" requests send this string and then get an "Uwu-ify" response, which translates some of the words in the given string to have "uwu" in them. And example of this would be sending an uwu request with the sentence "Hey there." which would promptly give you the response "Hey thewe. uwu"

"Flip-ify" request instead send the user string and get a "Flip-ify" response, which simply flips all the characters in the string and returns it. Example of this would be sending "Hey there." and receiving ".ereht yeH"

## Task 3
Not implemented.

# VIDEO LINK
link is included in the vid.txt file

# GIVEN README INFORMATION
## GRPC Services and Registry
The following folder contains a Registry.jar which includes a Registering service where Nodes can register to allow clients to find them and use their implemented GRPC services. 

Some more detailed explanations will follow and please also check the build.gradle file

Before starting do a "gradle generateProto".

### gradle runRegistryServer
Will run the Registry node on localhost (arguments are possible see gradle). This node will run and allows nodes to register themselves. 

The Server allows Protobuf, JSON and gRPC. We will only be using gRPC

### gradle runNode
Will run a node with an Echo and Joke service. The node registers itself on the Registry. You can change the host and port the node runs on and this will register accordingly with the Registry

### gradle runClientJava
Will run a client which will call the services from the node, it talks to the node directly not through the registry. At the end the client does some calls to the Registry to pull the services, this will be needed later.

### gradle runDiscovery
Will create a couple of threads with each running a node with services in JSON and Protobuf. This is just an example and not needed for assignment 6. 

### gradle testProtobufRegistration
Registers the protobuf nodes from runDiscovery and do some calls. 

### gradle testJSONRegistration
Registers the json nodes from runDiscovery and do some calls. 
