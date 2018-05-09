from BankClient import BankClient
import sys

if __name__ == '__main__':
    app = BankClient()
    sys.exit(app.main(sys.argv, "config.client"))
