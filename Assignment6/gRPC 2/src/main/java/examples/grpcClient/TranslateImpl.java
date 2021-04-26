package examples.grpcClient;

import io.grpc.stub.StreamObserver;
import service.*;


public class TranslateImpl extends TranslateGrpc.TranslateImplBase {

    @Override
    public void uwu(UwuRequest request, StreamObserver<UwuResponse> responseObserver) {
        System.out.println("Received from client: " + request.getToUwu());
        UwuResponse.Builder response = UwuResponse.newBuilder();
        String output =  uwuTranslation(request.getToUwu());
        response.setUwu(output);

        UwuResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();

    }

    @Override
    public void flip(FlipRequest request, StreamObserver<FlipResponse> responseObserver) {
        System.out.println("Received from client: " + request.getToFlip());
        FlipResponse.Builder response = FlipResponse.newBuilder();

        String output = new StringBuilder(request.getToFlip()).reverse().toString();

        response.setFlip(output);

        FlipResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();

    }


    public static String uwuTranslation(String input) {
        return input
                .replace('l', 'w')
                .replace('r', 'w')
                .replace('L', 'W')
                .replace('R', 'W')

                .replaceAll("\\.[^\\s]|\\z", " uwu");

    }
}
