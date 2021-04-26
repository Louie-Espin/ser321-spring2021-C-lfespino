package examples.grpcClient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import service.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
    private final EchoGrpc.EchoBlockingStub echoBlockingStub;
    private final JokeGrpc.JokeBlockingStub jokeBlockingStub;
    private final RegistryGrpc.RegistryBlockingStub registryBlockingStub;
    private final StoryGrpc.StoryBlockingStub storyBlockingStub;
    private final CalcGrpc.CalcBlockingStub calcBlockingStub;
    private final TranslateGrpc.TranslateBlockingStub translateBlockingStub;

    /**
     * Construct client for accessing server using the existing channel.
     */
    public EchoClient(Channel channel, Channel regChannel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
        // responsibility to shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.

        echoBlockingStub = EchoGrpc.newBlockingStub(channel);
        jokeBlockingStub = JokeGrpc.newBlockingStub(channel);
        storyBlockingStub = StoryGrpc.newBlockingStub(channel);
        calcBlockingStub = CalcGrpc.newBlockingStub(channel);
        translateBlockingStub = TranslateGrpc.newBlockingStub(channel);
        registryBlockingStub = RegistryGrpc.newBlockingStub(regChannel);
    }



    public void askServerToParrot(String message) {
        System.out.println("\n~~Echo service!~~");
        ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
        ServerResponse response;
        try {
            response = echoBlockingStub.parrot(request);
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
        System.out.println("Received from server: " + response.getMessage());
    }



    public void askForJokes(int num) {
        System.out.println("\n~~Get joke service!~~");
        JokeReq request = JokeReq.newBuilder().setNumber(num).build();
        JokeRes response;

        try {
            response = jokeBlockingStub.getJoke(request);
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
        System.out.println("Joke list: ");
        for (String joke : response.getJokeList()) {
            System.out.println("---"+joke);
        }
    }

    public void setJoke(String joke) {
        System.out.println("\n~~Add joke service!~~");
        JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
        JokeSetRes response;

        try {
            response = jokeBlockingStub.setJoke(request);
            System.out.println("Added: "+joke);
            System.out.println(response.getOk()); // Responding that joke was added
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }



    public void readStory() {
        System.out.println("\n~~Reading service!~~");
        Empty request = Empty.newBuilder().build();
        ReadResponse response;
        try {
            response = storyBlockingStub.read(request);
            System.out.println("The story: "+response.getSentence());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void writeStory(String sentence) {
        System.out.println("\n~~Writing service!~~");
        WriteRequest request = WriteRequest.newBuilder().setNewSentence(sentence).build();
        WriteResponse response;
        try {
            response = storyBlockingStub.write(request);
            System.out.println("The new story: "+response.getStory());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }



    public void add(ArrayList<Double> arr) {
        System.out.println("\n~~Addition service!~~");
        CalcRequest request = CalcRequest.newBuilder().addAllNum(arr).build();
        CalcResponse response;
        try {
            response = calcBlockingStub.add(request);
            System.out.println("Solution: "+response.getSolution());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }

    public void subtract(ArrayList<Double> arr) {
        System.out.println("\n~~Subtraction service!~~");
        CalcRequest request = CalcRequest.newBuilder().addAllNum(arr).build();
        CalcResponse response;
        try {
            response = calcBlockingStub.subtract(request);
            System.out.println("Solution: "+response.getSolution());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }

    public void multiply(ArrayList<Double> arr) {
        System.out.println("\n~~Multiplication service!~~");
        CalcRequest request = CalcRequest.newBuilder().addAllNum(arr).build();
        CalcResponse response;
        try {
            response = calcBlockingStub.multiply(request);
            System.out.println("Solution: "+response.getSolution());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }

    }

    public void divide(ArrayList<Double> arr) {
        System.out.println("\n~~Division service!~~");
        CalcRequest request = CalcRequest.newBuilder().addAllNum(arr).build();
        CalcResponse response;
        try {
            response = calcBlockingStub.divide(request);
            System.out.println("Solution: "+response.getSolution());
            //System.out.println(response.getIsSuccess());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }



    public void uwuTranslate(String uwu) {
        System.out.println("\n~~UWUing service!~~");
        UwuRequest request = UwuRequest.newBuilder().setToUwu(uwu).build();
        UwuResponse response;
        try {
            response = translateBlockingStub.uwu(request);
            System.out.println("Translation: ");
            System.out.println(response.getUwu());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }

    public void flipTranslate(String flip) {
        System.out.println("\n~~Flipping service!~~");
        FlipRequest request = FlipRequest.newBuilder().setToFlip(flip).build();
        FlipResponse response;
        try {
            response = translateBlockingStub.flip(request);
            System.out.println("Translation: ");
            System.out.println(response.getFlip());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
        }
    }



    public void getServices() {
        GetServicesReq request = GetServicesReq.newBuilder().build();
        ServicesListRes response;
        try {
            response = registryBlockingStub.getServices(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void findServer(String name) {
        FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
        SingleServerRes response;
        try {
            response = registryBlockingStub.findServer(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public void findServers(String name) {
        FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
        ServerListRes response;
        try {
            response = registryBlockingStub.findServers(request);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.err.println("RPC failed: " + e);
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.out.println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <auto(int)> ");
            System.exit(1);
        }
        int port = 9099;
        int regPort = 9003;
        String host = args[0];
        String regHost = args[2];
        String message = args[4];
        int auto = 0;
        try {
            port = Integer.parseInt(args[1]);
            regPort = Integer.parseInt(args[3]);
            auto = Integer.parseInt(args[5]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe and reusable.
        // It is common to create channels at the beginning of your application and reuse them until the application shuts down.

        String target = host + ":" + port;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext().build();

        String regTarget = regHost + ":" + regPort;
        ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
        try {
            // ##############################################################################
            // Assume we know the port here from the service node it is basically set through Gradle here.
            // In your version you should first contact the registry to check which services are available and what the port etc is.

            /**
             * Your client should start off with
             * 1. contacting the Registry to check for the available services
             * 2. List the services in the terminal and the client can
             *    choose one (preferably through numbering)
             * 3. Based on what the client chooses
             *    the terminal should ask for input, eg. a new sentence, a sorting array or
             *    whatever the request needs
             * 4. The request should be sent to one of the
             *    available services (client should call the registry again and ask for a
             *    Server providing the chosen service) should send the request to this service and
             *    return the response in a good way to the client
             *
             * You should make sure your client does not crash in case the service node
             * crashes or went offline.
             */

            // Just doing some hard coded calls to the service node without using the registry create client
            EchoClient client = new EchoClient(channel, regChannel);

            if (auto == 0) {
                while (client.menu(client));
            }
            else {
                // Parrot service
                client.askServerToParrot(message);
                client.askServerToParrot("Another message to parrot");

                // Joke service
                client.askForJokes(5);
                client.setJoke("I made a pencil with two erasers. It was pointless.");
                client.askForJokes(10);

                // Story service
                client.readStory();
                client.writeStory("I just added a sentence to the story!");

                // Calc service
                ArrayList<Double> numbers = new ArrayList<>();
                numbers.add(50.0);
                numbers.add(10.0);
                numbers.add(20.0);

                client.add(numbers);
                client.subtract(numbers);
                client.multiply(numbers);
                client.divide(numbers);

                // Translate service
                client.uwuTranslate("Hello my name is Luis!");
                client.flipTranslate("Hello my name is Luis!");

            }

            // ############### Contacting the registry just so you see how it can be done
            // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running get thread's services

//            client.getServices();
//            // get parrot
//            client.findServer("services.Echo/parrot");
//            // get all setJoke
//            client.findServers("services.Joke/setJoke");
//            // get getJoke
//            client.findServer("services.Joke/getJoke");
//            // does not exist
//            client.findServer("random");


        }
        finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these resources the channel
            // should be shut down when it will no longer be used. If it may be used again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }


    public boolean menu(EchoClient client) {

        System.out.println("\nPlease choose the service");
        System.out.println("e - Echo service");
        System.out.println("j - Joke service");
        System.out.println("s - Story service");
        System.out.println("c - Calc service");
        System.out.println("t - Translate service");
        System.out.println("x - exit");

        // ask the user for input how many jokes the user wants
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String choice = reader.readLine();
            switch (choice.toLowerCase()) {
                case "e":
                    System.out.println("Enter a sentence to be echoed: ");
                    String message = reader.readLine();
                    client.askServerToParrot(message);
                    break;
                case "j":
                    System.out.println("Enter the amount of jokes to get: ");
                    String num = reader.readLine();
                    try {
                        int n = Integer.parseInt(num);
                        client.askForJokes(n);
                    }
                    catch (Exception e) {
                        System.out.println("Input not valid! Please enter an int");
                        break;
                    }

                    System.out.println("Enter your joke: ");
                    String joke = reader.readLine();
                    if (joke.length() > 0) {
                        client.setJoke(joke);
                    }
                    else {
                        System.out.println("Nothing was added!");
                    }
                    break;
                case "s":
                    client.readStory();
                    System.out.println("\nEnter your sentence for the story: ");
                    String sentence = reader.readLine();
                    if (sentence.length() > 0) {
                        client.writeStory(sentence);
                    }
                    else {
                        System.out.println("Nothing was added!");
                    }
                    break;
                case "c":
                    ArrayList<Double> doubles = new ArrayList<>();
                    while (true) {
                        System.out.println("\nEnter a double: ");
                        System.out.println("Press 'Enter' to stop");
                        try {
                            Double n = Double.parseDouble(reader.readLine());
                            doubles.add(n);
                        }
                        catch (Exception e) {
                            break;
                        }
                    }
                    System.out.println("\nChoose your calculation type:");
                    System.out.println("a - Addition");
                    System.out.println("s - Subtraction");
                    System.out.println("m - Multiplication");
                    System.out.println("d - Division");

                    String cChoice = reader.readLine();
                    switch (cChoice.toLowerCase()) {
                        case "a":
                            client.add(doubles);
                            break;
                        case "s":
                            client.subtract(doubles);
                            break;
                        case "m":
                            client.multiply(doubles);
                            break;
                        case "d":
                            client.divide(doubles);
                            break;
                        default:
                            System.out.println("\nInvalid option: "+choice);
                            break;
                    }
                    break;
                case "t":
                    System.out.println("\nEnter sentence to translate: ");
                    String words = reader.readLine();

                    System.out.println("\nChoose what to translate to:");
                    System.out.println("u - uwu-ify");
                    System.out.println("f - flip-ify");

                    String tChoice = reader.readLine();

                    switch (tChoice.toLowerCase()) {
                        case "u":
                            client.uwuTranslate(words);
                            break;
                        case "f":
                            client.flipTranslate(words);
                            break;
                        default:
                            System.out.println("\nInvalid option: "+choice);
                            break;
                    }
                    break;
                case "x":
                    return false;
                default:
                    System.out.println("\nInvalid option: "+choice);
                    break;
            }

        } catch (IOException e) {
            System.out.println("Unable to receive data");
            return false;
        }
        return true;
    }



}
