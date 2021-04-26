package examples.grpcClient;

import io.grpc.stub.StreamObserver;
import service.*;

public class StoryImpl extends StoryGrpc.StoryImplBase {

    // having a global set of jokes
    String story = "";

    @Override
    public void read(Empty req, StreamObserver<ReadResponse> responseObserver) {

        System.out.println("Received from client: " + req);
        ReadResponse.Builder response = ReadResponse.newBuilder();
        try {
            response.setSentence(story);
            response.setIsSuccess(true);
        }
        catch (Exception e) {
            response.setSentence(story);
            response.setIsSuccess(false);
        }

        ReadResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }


    @Override
    public void write(WriteRequest req, StreamObserver<WriteResponse> responseObserver) {

        System.out.println("Received from client: " + req.getNewSentence());
        WriteResponse.Builder response = WriteResponse.newBuilder();
        try {
            story = story.concat(" "+req.getNewSentence());
            response.setIsSuccess(true);
            response.setStory(story);
        }
        catch (Exception e) {
            response.setIsSuccess(false);
            response.setStory(story);
        }

        WriteResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

}
