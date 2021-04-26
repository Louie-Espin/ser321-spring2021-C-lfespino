package examples.grpcClient;

import io.grpc.stub.StreamObserver;

import service.*;

import java.util.ArrayList;


// Implement the joke service. It has two sevices getJokes and setJoke
class JokeImpl extends JokeGrpc.JokeImplBase {

    // having a global set of jokes
    ArrayList<String> list = new ArrayList<>();

    public JokeImpl() {
        super();
        // copying some dad jokes
        list.add("How do you get a squirrel to like you? Act like a nut.");
        list.add("I don't trust stairs. They're always up to something.");
        list.add("What do you call someone with no body and no nose? Nobody knows.");
        list.add("Did you hear the rumor about butter? Well, I'm not going to spread it!");

    }

    // We are reading how many jokes the clients wants and put them in a list to send back to client
    @Override
    public void getJoke(JokeReq req, StreamObserver<JokeRes> responseObserver) {
        System.out.println("Received from client: " + req.getNumber());
        JokeRes.Builder response = JokeRes.newBuilder();
        for (int i = 0; i < req.getNumber(); i++) {
            if (i < list.size()) {
                response.addJoke(list.get(i));
            }
        }
        JokeRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    // We take the joke the user wants to set and put it in our set of jokes
    @Override
    public void setJoke(JokeSetReq req, StreamObserver<JokeSetRes> responseObserver) {
        System.out.println("Received from client: " + req.getJoke());
        list.add(req.getJoke());
        JokeSetRes.Builder response = JokeSetRes.newBuilder();
        response.setOk(true);

        JokeSetRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}