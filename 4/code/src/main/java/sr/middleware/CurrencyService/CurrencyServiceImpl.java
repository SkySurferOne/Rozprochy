package sr.middleware.CurrencyService;

import io.grpc.stub.StreamObserver;
import sr.middleware.proto.*;

import java.util.Map;

public class CurrencyServiceImpl extends CurrencyServiceGrpc.CurrencyServiceImplBase {
    private boolean isStreaming = true;
    private long refreshTime = 5000;

    @Override
    public void getCurrencyStatusCollection(CurrencyTypeCollection request,
                                            StreamObserver<CurrencyStatusCollection> responseObserver) {

    }

    @Override
    public void getCurrencyStatusCollectionStream(CurrencyTypeCollection request,
                                                  StreamObserver<CurrencyStatusCollection> responseObserver) {
        System.out.println("Start streaming currency status...");

        while (isStreaming) {
            CurrencyStatusCollection.Builder currencyStatusCollectionBuilder = CurrencyStatusCollection.newBuilder();
            Map<CurrencyType, CurrencyEntity> map = CurrencyRepository.getFiltered(request);

            for (Map.Entry<CurrencyType, CurrencyEntity> entry : map.entrySet()) {
                CurrencyStatus currencyStatus = CurrencyStatus.newBuilder().setCurrencyType(entry.getKey())
                        .setPurchaseValue(entry.getValue().getPurchaseValue())
                        .setSaleValue(entry.getValue().getSaleValue())
                        .build();
                currencyStatusCollectionBuilder.addCurrencyStatus(currencyStatus);
            }
            CurrencyStatusCollection currencyStatusCollection = currencyStatusCollectionBuilder.build();
            responseObserver.onNext(currencyStatusCollection);

            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("End of streaming.");
        isStreaming = true;
        responseObserver.onCompleted();
    }
}
