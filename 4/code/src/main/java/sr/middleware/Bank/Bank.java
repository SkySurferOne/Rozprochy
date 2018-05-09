package sr.middleware.Bank;

import Ice.Identity;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sr.middleware.CurrencyService.CurrencyEntity;
import sr.middleware.proto.CurrencyServiceGrpc;
import sr.middleware.proto.CurrencyTypeCollection;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Bank implements Runnable {
    private static final Logger logger = Logger.getLogger(Bank.class.getName());

    private final CurrencyTypeCollection currencyTypeCollection;

    private ManagedChannel channel;
    private CurrencyServiceGrpc.CurrencyServiceBlockingStub currencyServiceBlockingStub;
    private CurrencyServiceGrpc.CurrencyServiceStub currencyServiceNonBlockingStub;
    private Ice.Communicator communicator = null;
    private String currencyServiceHost;
    private int currencyServicePort;
    private String bankName;
    private String bankServerHost;
    private String bankServerPort;

    private ConcurrentHashMap<String, CurrencyEntity> currencyStorage = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, UserEntity> userStorage = new ConcurrentHashMap<>();

    public Bank(String bankName, CurrencyTypeCollection currencyTypeCollection,
                String bankServerHost, String bankServerPort,
                String currencyServiceHost, int currencyServicePort) {
        this.bankName = bankName;
        this.currencyTypeCollection = currencyTypeCollection;
        this.bankServerHost = bankServerHost;
        this.bankServerPort = bankServerPort;
        this.currencyServicePort = currencyServicePort;
        this.currencyServiceHost = currencyServiceHost;
    }

    public void run() {
        configCurrencyServiceConnection();
        configBankServer();
    }

    private void configBankServer() {
        communicator = Ice.Util.initialize();
        String s1 = "tcp -h "+bankServerHost+" -p "+bankServerPort+":udp -h "+bankServerHost+" -p "+bankServerPort;
        Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1", s1);
        logger.info("Server is listening on "+bankServerHost+":"+bankServerPort);

        FactoryImpl accountFactory = new FactoryImpl(userStorage, currencyStorage, adapter,
                communicator, bankServerHost, bankServerPort);
        adapter.add(accountFactory, new Identity("accountFactory", "factory"));
        adapter.activate();

        System.out.println("Entering event processing loop...");

        communicator.waitForShutdown();
    }

    private void configCurrencyServiceConnection() {
        channel = ManagedChannelBuilder.forAddress(currencyServiceHost, currencyServicePort)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext(true)
                .build();

        currencyServiceBlockingStub = CurrencyServiceGrpc.newBlockingStub(channel);
        currencyServiceNonBlockingStub = CurrencyServiceGrpc.newStub(channel);
        CurrencyStorageRefresher currencyStorageRefresher = new CurrencyStorageRefresher(currencyStorage,
                currencyServiceBlockingStub, currencyTypeCollection);
        new Thread(currencyStorageRefresher).start();
    }

}
