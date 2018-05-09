package sr.middleware.CurrencyService;

import sr.middleware.proto.CurrencyType;
import sr.middleware.proto.CurrencyTypeCollection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrencyRepository {
    private static final Map<CurrencyType, CurrencyEntity> db = new HashMap<>();

    static {
        db.put(CurrencyType.PLN, new CurrencyEntity(1.0, 1.0, "PLN"));
        db.put(CurrencyType.EUR, new CurrencyEntity(4.2401, 4.3257, "EUR"));
        db.put(CurrencyType.USD, new CurrencyEntity(3.5412, 3.6112, "USD"));
        db.put(CurrencyType.CZK, new CurrencyEntity(0.1656, 0.1690, "CZK"));
        db.put(CurrencyType.GBP, new CurrencyEntity(4.8168, 4.9142, "GPB"));
    }

    public static  Map<CurrencyType, CurrencyEntity> getAll() {
        return db.entrySet()
                .stream()
                .map(CurrencyRepository::addVariation)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<CurrencyType, CurrencyEntity> getFiltered(CurrencyTypeCollection currencyTypeCollection) {
        List<CurrencyType> list = currencyTypeCollection.getCurrencyTypeList();
        return db.entrySet()
                .stream()
                .filter(entry -> list.contains(entry.getKey()))
                .map(CurrencyRepository::addVariation)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<CurrencyType, CurrencyEntity> addVariation(Map.Entry<CurrencyType, CurrencyEntity> entry) {
        double maxDelta = 0.2;
        double minDelta = -0.2;
        double delta = random(minDelta, maxDelta);
        CurrencyEntity currencyEntityOld = entry.getValue();
        CurrencyEntity currencyEntity = new CurrencyEntity(currencyEntityOld.getPurchaseValue() + delta,
                currencyEntityOld.getSaleValue() + delta,
                currencyEntityOld.getName());
        entry.setValue(currencyEntity);
        return entry;

    }

    private static double random(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
}