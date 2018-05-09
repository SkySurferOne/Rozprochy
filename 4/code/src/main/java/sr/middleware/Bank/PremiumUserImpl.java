package sr.middleware.Bank;

import Ice.Current;
import sr.middleware.slice.CurrencyType;
import sr.middleware.slice.LoanInfo;
import sr.middleware.slice._PremiumUserDisp;

public class PremiumUserImpl extends _PremiumUserDisp {
    @Override
    public LoanInfo getLoanInfo(CurrencyType currencyType, double loanValue, int period, Current __current) {
        return null;
    }

    @Override
    public long checkAccountStatus(Current __current) {
        return 0;
    }
}
