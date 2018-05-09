package sr.middleware.Bank;

import Ice.Current;
import sr.middleware.CurrencyService.CurrencyEntity;
import sr.middleware.slice.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PremiumUserImpl extends _PremiumUserDisp {
    private final ConcurrentHashMap<UUID, UserEntity> userStorage;
    private ConcurrentHashMap<String, CurrencyEntity> currencyStorage;

    public PremiumUserImpl(ConcurrentHashMap<UUID, UserEntity> userStorage,
                           ConcurrentHashMap<String, CurrencyEntity> currencyStorage) {
        this.userStorage = userStorage;
        this.currencyStorage = currencyStorage;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double calculateLoanRate(double interestRate, double loanValue, int period) {
            double q = 1 + (interestRate / 1200.0);
            double val = loanValue * (Math.pow(q, period) * ((1 - q) / (1 - Math.pow(q, period))));
            return roundTwoDecimals(val);
    }

    @Override
    public LoanInfo getLoanInfo(CurrencyType currencyType, double loanValue, int period, Current __current)
            throws NotSupportedCurrency {

        if (currencyStorage.get(currencyType.toString()) == null) {
            throw new NotSupportedCurrency();
        }

        double interestRate;
        if (period < 12) {
            interestRate = 4;
        } else if (period < 24) {
            interestRate = 5;
        } else {
            interestRate = 7;
        }

        CurrencyEntity currencyEntity = currencyStorage.get(currencyType.toString());
        double loanRateOtherCurrency = calculateLoanRate(interestRate, loanValue, period);
        double avgCurrencyRate = (currencyEntity.getPurchaseValue() + currencyEntity.getSaleValue()) / 2.0;
        double loanRateBaseCurrency = roundTwoDecimals(loanRateOtherCurrency * avgCurrencyRate);

        return new LoanInfo(loanRateBaseCurrency, loanRateOtherCurrency, currencyType, period,
                avgCurrencyRate, loanValue, interestRate);
    }

    @Override
    public double checkAccountStatus(String id, Current __current) throws UserDoesNotExist {
        UserEntity userEntity = userStorage.get(UUID.fromString(id));

        if (userEntity == null) {
            throw new UserDoesNotExist();
        }

        return userEntity.getAccountState();
    }
}
