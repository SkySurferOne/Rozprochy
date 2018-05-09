#!/usr/bin/env python

import sys, traceback, Ice

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


class BankClient(Ice.Application):

    def __init__(self):
        self.twoway = None
        self.user = None
        Ice.Application.__init__(self, Ice.Application.NoSignalHandling)

    def run(self, args):
        if len(args) > 1:
            print(self.appName() + ": too many arguments")
            return 1

        self.twoway = Slice.FactoryPrx.checkedCast(
            self.communicator().propertyToProxy('Factory.Proxy').ice_twoway().ice_secure(False))
        if not self.twoway:
            print(args[0] + ": invalid proxy")
            return 1

        # oneway = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_oneway())
        # batchOneway = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_batchOneway())
        # datagram = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_datagram())
        # batchDatagram = Slice.FactoryPrx.uncheckedCast(self.twoway.ice_batchDatagram())

        c = None
        while c != 'exit':
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

            except KeyboardInterrupt:
                break
            except EOFError:
                break
            except Ice.Exception as ex:
                print(ex)

        return 0

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