package sr.middleware.Bank;

import io.grpc.StatusRuntimeException;
import sr.middleware.CurrencyService.CurrencyEntity;
import sr.middleware.proto.CurrencyServiceGrpc;
import sr.middleware.proto.CurrencyStatus;
import sr.middleware.proto.CurrencyStatusCollection;
import sr.middleware.proto.CurrencyTypeCollection;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrencyStorageRefresher implements Runnable {
    private static final Logger logger = Logger.getLogger(CurrencyStorageRefresher.class.getName());

    private ConcurrentHashMap<String, CurrencyEntity> currencyStorage;
    private CurrencyServiceGrpc.CurrencyServiceBlockingStub currencyServiceBlockingStub;
    private final CurrencyTypeCollection currencyTypeCollection;

    public CurrencyStorageRefresher(ConcurrentHashMap<String, CurrencyEntity> currencyStorage,
                                    CurrencyServiceGrpc.CurrencyServiceBlockingStub currencyServiceBlockingStub,
                                    CurrencyTypeCollection currencyTypeCollection) {
        this.currencyStorage = currencyStorage;
        this.currencyServiceBlockingStub = currencyServiceBlockingStub;
        this.currencyTypeCollection = currencyTypeCollection;
    }

    @Override
    public void run() {
        runGetCurrencyStatusCollectionStream();
    }

    public void runGetCurrencyStatusCollectionStream() {
        Iterator<CurrencyStatusCollection> currencyStatusCollectionIterator;
        try {
            currencyStatusCollectionIterator = currencyServiceBlockingStub.getCurrencyStatusCollectionStream(currencyTypeCollection);
            while (currencyStatusCollectionIterator.hasNext()) {
                CurrencyStatusCollection currencyStatusCollection = currencyStatusCollectionIterator.next();
                List<CurrencyStatus> list = currencyStatusCollection.getCurrencyStatusList();

                for (CurrencyStatus currencyStatus : list) {
                    String name = currencyStatus.getCurrencyType().toString();
                    double puchraseValue = currencyStatus.getPurchaseValue();
                    double saleValuse = currencyStatus.getSaleValue();

                    CurrencyEntity currencyEntity = new CurrencyEntity(puchraseValue, saleValuse, name);
                    currencyStorage.put(name, currencyEntity);

                    // logger.info(name + ", puchrase value: "+puchraseValue+", sale value: "+saleValuse);
                }


            }
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
        }
    }
}
