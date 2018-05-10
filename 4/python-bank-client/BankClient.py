#!/usr/bin/env python

import sys, Ice

Ice.loadSlice('../code/src/main/slice/CurrBankCommInt.ice')
# import generated_sources.sr.middleware.slice as Slice
import sr.middleware.slice as Slice


class UserAccount():

    def __init__(self, id, proxy, is_premium):
        self.id = id
        self.proxy = proxy
        self.is_premium = is_premium

    def check_account_status(self):
        return self.proxy.checkAccountStatus(self.id)

    def get_loan_info(self, currency_type, loan_value, period):
        return self.proxy.getLoanInfo(currency_type, loan_value, period)

    def is_premium(self):
        return self.is_premium


class BankClient(Ice.Application):

    def __init__(self):
        self.twoway = None
        self.user = None
        Ice.Application.__init__(self, Ice.Application.NoSignalHandling)

    def help(self):
        print('Help info\n'
              '\tcreate-account (ca) - creates accout\n'
              '\tlog-in (li) - login into system\n'
              '\taccount-status (as) - check account status\n'
              '\ttake-loan (tl) - take a loan (for premium users)\n'
              '\texit (x)- exits the program')

    def run(self, args):
        if len(args) < 2:
            print('To less arguments')
            return

        self.twoway = Slice.FactoryPrx.checkedCast(
            self.communicator().propertyToProxy('Factory'+args[1]+'.Proxy').ice_twoway().ice_secure(False))
        if not self.twoway:
            print(args[0] + ": invalid proxy")
            return 1

        # oneway = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_oneway())
        # batchOneway = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_batchOneway())
        # datagram = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_datagram())
        # batchDatagram = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_batchDatagram())

        c = None
        while c != 'exit' and c != 'x':
            try:
                sys.stdout.write("==> ")
                sys.stdout.flush()
                c = sys.stdin.readline().strip()

                if c == 'create-account' or c == 'ca':
                    self.create_account()

                if c == 'log-in' or c == 'li':
                    self.login()

                if c == 'account-status' or c == 'as':
                    self.account_status()

                if c == 'take-loan' or c == 'tl':
                    self.loan_info()

                if c == 'help' or c == 'h':
                    self.help()

            except KeyboardInterrupt:
                break
            except EOFError:
                break
            except Ice.Exception as ex:
                print(ex)

        return 0

    def get_currency(self, name):
        name = name.upper()
        if name == 'PLN':
            return Slice.CurrencyType.PLN
        elif name == 'EUR':
            return Slice.CurrencyType.EUR
        elif name == 'USD':
            return Slice.CurrencyType.USD
        elif name == 'CZK':
            return Slice.CurrencyType.CZK
        elif name == 'GBP':
            return Slice.CurrencyType.GBP
        else:
            return None

    def loan_info(self):
        if self.user is None:
            print('You cannot perform this operation. You have to login to premium account.')
            return

        if not self.user.is_premium:
            print('You cannot perform this operation. You have to have a premium account.')
            return

        try:
            print('Write loan information')
            print('Currency: ', end='', flush=True)
            currency = sys.stdin.readline().strip()
            currency_enum = self.get_currency(currency)
            if currency_enum is None:
                raise CurrencyDoesNotExist()
            print('Loan value: ', end='', flush=True)
            loan_value = float(sys.stdin.readline().strip())
            print('Period (in months): ', end='', flush=True)
            period = int(sys.stdin.readline().strip())

            loan_info = self.user.get_loan_info(currency_enum, loan_value, period)

            print('Loan value: '+str(loan_info.loanValue)+' '+loan_info.currencyType.__str__())
            print('For period (months): '+str(loan_info.period))
            print('Loan installment (other currency): '+str(loan_info.loanRateOtherCurrency)+' '+
                  loan_info.currencyType.__str__())
            print('Loan installment (base currency): '+str(loan_info.loanRateBaseCurrency)+' PLN')
            print('Interest rate: '+str(loan_info.interestRate)+'%')
            print(loan_info.currencyType.__str__()+' exchange rate: '+str(loan_info.avgExchangeRate))
            print('Total cost: '+str(loan_info.period*loan_info.loanRateOtherCurrency)+' '+
                  loan_info.currencyType.__str__())
            print('Total cost: '+str(loan_info.period*loan_info.loanRateBaseCurrency)+' PLN')

        except ValueError:
            print('That was no valid number')
        except CurrencyDoesNotExist:
            print('Typed currency symbol does not exist')
        except Slice.NotSupportedCurrency:
            print('This currency is not supported in this bank')

    def account_status(self):
        if self.user is None:
            print("You are not logged in")
        else:
            account_status = self.user.check_account_status()
            print("Your account status: "+str(account_status))

    def login(self):
        print('Write your account id: ', end='', flush=True)
        id = sys.stdin.readline().strip()

        try:
            user_proxy = self.twoway.logInAsPremiumUser(id)
            # print(user_proxy)
            premium = True
            self.user = UserAccount(id, user_proxy, premium)
            print('Logged as premium user')
        except Slice.UserDoesNotExist:
            print("User with that id does not exist")
        except Slice.PermissionViolation:
            user_proxy = self.twoway.logInAsStandardUser(id)
            premium = False
            self.user = UserAccount(id, user_proxy, premium)
            print('Logged as standard user')

    def create_account(self):
        print('Creating account')
        print('Write your first name: ', end='', flush=True)
        firstname = sys.stdin.readline().strip()
        print('Write your last name: ', end='', flush=True)
        lastname = sys.stdin.readline().strip()
        print('Write your PESEL number: ', end='', flush=True)
        pesel = sys.stdin.readline().strip()
        print('Write your monthly income: ', end='', flush=True)
        monthly_income = int(sys.stdin.readline().strip())

        user_info = Slice.UserInfo(firstname, lastname, pesel, monthly_income)
        id = self.twoway.createAccount(user_info)
        print('Your id: '+id)


class CurrencyDoesNotExist(Exception):
    pass
