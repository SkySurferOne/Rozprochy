package sr.middleware.CurrencyService;

public class CurrencyEntity {
    private final double purchaseValue;
    private final double saleValue;
    private final String name;

    public CurrencyEntity(double purchaseValue, double saleValue, String name) {
        this.purchaseValue = purchaseValue;
        this.saleValue = saleValue;
        this.name = name;
    }

    public double getPurchaseValue() {
        return purchaseValue;
    }

    public double getSaleValue() {
        return saleValue;
    }

    public String getName() {
        return name;
    }
}
