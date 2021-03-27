# Assignment 4 Activity 1
## Description
This code displays 3 different kinds of servers:
- Task 1: a single threaded server with JSON protocol calls.
- Task 2: a multi-threaded server with an uncapped amount of clients that may access it.
- Task 3: a bounded multi-threaded server with a specified amount of threads that may run at once.

## Protocol
### Requests
request: { "selected": <int: 1=add, 2=pop, 3=display, 4=count, 5=switch,
0=quit>, "data": <thing to send>}

  data <string>: add
  data <int> pop
  data <int> <int> switch but send as String
  data "" count, quit, and switch have an empty String

### Responses

sucess response: {"type": <"add", "pop", "display", "count", "switch", "quit"> "data": <thing to return> }

type <String>: echoes original selected from request
data <string>: add = new list, pop = new list, display = current list, count = num elements, switch = new list with switched elements


error response: {"type": "error", "error"": <error string> }
Should give good error message if something goes wrong


## How to run the program
### Terminal
Base Code, please use the following commands:
```
    For Server, run "gradle runServer -Pport=9099 -q --console=plain"
```
```   
    For Client, run "gradle runClient -Phost=localhost -Pport=9099 -q --console=plain"
```
```
    For Task 1, run with: gradle runTask1 -Pport=[num]
    For default port 8000, you may also run: gradle runTask1
```
```
    For Task 2, run with: gradle runTask2 -Pport=[num]
    For default port 8000, you may also run: gradle runTask2
```
```
    For Task 3, run with: gradle runTask3 -Pport=[num] -Pbound=[num]
    For default port 8000, you may also run: gradle runTask3 -Pbound=[num]
    For default port 8000 and bound 10, run: gradle runTask3
```

## Requirements Met
### Task 1
Every requirement in task 1 has been met. Once the server is running, the client may add, pop, display, count, or switch elements in the string list.
- add: The client may add a string by typing 1. The server then adds it to the list and displays it back to the client.
- pop: The client may pop a string from the list with 2. The server removes the top element from the list, then displays it to the client. If the list is empty, the server sends null.
- display: The client may display the string list with 3. The server then displays the current list.
- count: The client may ask for the string count with 4. The server then sends an integer.
- switch: The client may ask to switch two list indexes around with 5. The server then switches both strings around, or sends "null" if the indexes are invalid.
- quit: The client can quit with 0. The server ends the connection and keeps running, waiting for other clients.
- misc: The program has error handling in case the server or client disconnect, or the client sends an invalid value, etc.

### Task 2
Every requirement in task 2 has been met.
- Task 2 uses the class "ThreadedServer" on package "tasktwo", as shown in the build file.
- This server has no bound on how many clients can be handled, and creates a new thread for each client connection.
- No clients block, and all clients can act at the same time. I used a synchronize block for changing data.
- The strings list is shared between clients, so all clients can change and view the same list.

### Task 3
Every requirement in task 3 has been met.
- Task 3 uses the class "ThreadedPoolServer" on package "taskthree", as shown in the build file.
- Users can specify the number of threads to be bound by, as shown in the "How to run the program" section.
- When the server reaches its max number of active threads, the following clients recieve a "Server Busy" error message and respond accordingly.

