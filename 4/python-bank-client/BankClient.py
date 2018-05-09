#!/usr/bin/env python

import sys, traceback, Ice

Ice.loadSlice('../code/src/main/slice/CurrBankCommInt.ice')
# import generated_sources.sr.middleware.slice as Slice
import sr.middleware.slice as Slice


class BankClient(Ice.Application):


    def __init__(self):
        self.twoway = None
        self.user_proxy = None
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

                if c == 'create-account':
                    self.create_account()

                if c == 'log-in':
                    self.login()

                if c == 'account-status':
                    self.account_status()

            except KeyboardInterrupt:
                break
            except EOFError:
                break
            except Ice.Exception as ex:
                print(ex)

        return 0

    def account_status(self):


    def login(self):
        print('Write your account id: ', end='', flush=True)
        id = sys.stdin.readline().strip()

        try:
            self.user_proxy = self.twoway.logInAsPremiumUser(id)
            print('Logged as premium user')
        except Slice.UserDoesNotExist:
            print("User with that id does not exist")
        except Slice.PermissionViolation:
            self.user_proxy = self.twoway.logInAsStandardUser(id)
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