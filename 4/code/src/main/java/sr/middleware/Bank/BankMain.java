package sr.middleware.Bank;

import sr.middleware.proto.CurrencyType;
import sr.middleware.proto.CurrencyTypeCollection;

public class BankMain {
    private final static int port = 50001;
    private final static String host = "localhost";

    public static void main(String[] args) {
        CurrencyTypeCollection currencyTypeCollection = fillCurrencyMap(args);
        Bank bank1 = new Bank("Bank1", currencyTypeCollection, host, port);
        new Thread(bank1).start();
    }

    public static CurrencyTypeCollection fillCurrencyMap(String[] args) {
        CurrencyTypeCollection.Builder currencyTypeCollectionBuilder = CurrencyTypeCollection.newBuilder();
        for (String arg : args) {
            if (arg.equals("PLN")) {
                currencyTypeCollectionBuilder.addCurrencyType(CurrencyType.PLN);
            }
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
