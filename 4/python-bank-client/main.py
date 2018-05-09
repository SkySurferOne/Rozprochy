from BankClient import BankClient
import sys

if __name__ == '__main__':
    """
    python3 main.py <bank_server_number>
    """
    if len(sys.argv) < 2:
        print("Provide number of a bank server")
    else:
        app = BankClient()
        sys.exit(app.main(sys.argv, "config.client"))
