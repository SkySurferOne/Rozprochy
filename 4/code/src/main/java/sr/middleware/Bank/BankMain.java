package sr.middleware.Bank;

import IceInternal.Ex;
import sr.middleware.proto.CurrencyType;
import sr.middleware.proto.CurrencyTypeCollection;

import java.util.Arrays;

public class BankMain {
    private final static int currencyServicePort = 50001;
    private final static String currencyServiceHost = "localhost";

    /**
     * Example arguments: localhost 10006 CZK USD GBP
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("There is no enough arguments");
            }
            String bankServerHost = args[0];
            String bankServerPort = args[1];

            CurrencyTypeCollection currencyTypeCollection = fillCurrencyMap(Arrays.copyOfRange(args, 2, args.length));
            Bank bank1 = new Bank("Bank1", currencyTypeCollection,
                    bankServerHost, bankServerPort,
                    currencyServiceHost, currencyServicePort);
            new Thread(bank1).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CurrencyTypeCollection fillCurrencyMap(String[] args) {
        CurrencyTypeCollection.Builder currencyTypeCollectionBuilder = CurrencyTypeCollection.newBuilder();
        currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.PLN);
        for (String arg : args) {
            if (arg.equals("USD")) {
                currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.USD);
            }
            if (arg.equals("CZK")) {
                currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.CZK);
            }
            if (arg.equals("GBP")) {
                currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.GBP);
            }
            if (arg.equals("EUR")) {
                currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.EUR);
            }
        }
        return currencyTypeCollectionBuilder.build();
    }
}
