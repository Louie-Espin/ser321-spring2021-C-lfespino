package examples.grpcClient;

import io.grpc.stub.StreamObserver;
import service.CalcGrpc;
import service.CalcRequest;
import service.CalcResponse;

import java.util.List;

public class CalcImpl extends CalcGrpc.CalcImplBase {

    @Override
    public void add(CalcRequest request, StreamObserver<CalcResponse> responseObserver) {
        System.out.println("Received from client: " + request.getNumList());
        double addSol = 0;

        try {
            List<Double> doubles = request.getNumList();
            for (Double iter: doubles) {
                addSol += iter;
            }

            CalcResponse response = CalcResponse.newBuilder().setSolution(addSol).setIsSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            CalcResponse response = CalcResponse.newBuilder().setError("Invalid list").setIsSuccess(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void subtract(CalcRequest request, StreamObserver<service.CalcResponse> responseObserver) {
        System.out.println("Received from client: " + request.getNumList());
        double subSol = 0;

        try {
            subSol = request.getNum(0);

            for (int i = 1; i < request.getNumCount(); i++) {
                subSol -= request.getNum(i);
            }

            CalcResponse response = CalcResponse.newBuilder().setSolution(subSol).setIsSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            CalcResponse response = CalcResponse.newBuilder().setError("Invalid list").setIsSuccess(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void multiply(CalcRequest request, StreamObserver<service.CalcResponse> responseObserver) {
        System.out.println("Received from client: " + request.getNumList());
        double mulSol = 0;

        try {
            mulSol = request.getNum(0);

            for (int i = 1; i < request.getNumCount(); i++) {
                mulSol *= request.getNum(i);
            }

            CalcResponse response = CalcResponse.newBuilder().setSolution(mulSol).setIsSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            CalcResponse response = CalcResponse.newBuilder().setError("Invalid list").setIsSuccess(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void divide(CalcRequest request, StreamObserver<service.CalcResponse> responseObserver) {
        System.out.println("Received from client: " + request.getNumList());
        double divSol = 0;

        try {
            divSol = request.getNum(0);
            double divBy = 0;

            for (int i = 1; i < request.getNumCount(); i++) {
                divBy += request.getNum(i);
            }
            divSol = divSol/divBy;

            CalcResponse response = CalcResponse.newBuilder().setSolution(divSol).setIsSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            CalcResponse response = CalcResponse.newBuilder().setError("Invalid list").setIsSuccess(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

}
