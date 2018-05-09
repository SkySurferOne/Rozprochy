#ifndef CALC_ICE
#define CALC_ICE

module sr
{
  module middleware {
    module slice {
        enum CurrencyType {
            PLN,
            EUR,
            USD,
            CZK,
            GBP
        };

        struct UserInfo {
            string firstname;
            string lastname;
            string peselNumber;
            long monthlyIncome;
        };

        struct LoanInfo {
            double loanRateBaseCurrency;
            double loanRateOtherCurrency;
            CurrencyType currencyType;
            int period;
            double avgExchangeRate;
            double loanValue;
            double interestRate;
        };

        exception PermissionViolation {};
        exception UserDoesNotExist {};
        exception NotSupportedCurrency {};

        interface StandardUser {
            double checkAccountStatus(string id) throws UserDoesNotExist;
        };

        interface PremiumUser extends StandardUser {
            LoanInfo getLoanInfo(CurrencyType currencyType, double loanValue, int period) throws NotSupportedCurrency;
        };

        interface Factory {
            string createAccount(UserInfo userInfo);
            StandardUser* logInAsStandardUser(string id) throws UserDoesNotExist;
            PremiumUser* logInAsPremiumUser(string id) throws UserDoesNotExist, PermissionViolation;
        };
    };
  };
};

#endif
