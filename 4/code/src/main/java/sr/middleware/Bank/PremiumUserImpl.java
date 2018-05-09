package sr.middleware.Bank;

import Ice.Current;
import sr.middleware.slice.CurrencyType;
import sr.middleware.slice.LoanInfo;
import sr.middleware.slice.UserDoesNotExist;
import sr.middleware.slice._PremiumUserDisp;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PremiumUserImpl extends _PremiumUserDisp {
    private final ConcurrentHashMap<UUID, UserEntity> userStorage;

    public PremiumUserImpl(ConcurrentHashMap<UUID, UserEntity> userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public LoanInfo getLoanInfo(CurrencyType currencyType, double loanValue, int period, Current __current) {
        return null;
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
