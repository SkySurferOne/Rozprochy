package sr.middleware.BankClient;

import java.io.IOException;

public class BankClient {
    private boolean logged = false;

    public BankClient() {
    }

    public void test() {
        boolean isRunning = true;
        String line = null;
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        do {
            try {
                System.out.println("Welcome in <name> bank.");
                System.out.println("Type 'create-account' to create account");

                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();
                isRunning = interpretCommand(line);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (isRunning);
    }

    public boolean interpretCommand(String line) {
        String[] command = line.split(" ");
        String baseCommand = command.length > 0 ? command[0] : "none";

        switch (baseCommand) {
            case "exit":
                return false;
            case "switch":

                break;
            case "create-account":
                accountCreator();
                break;
            case "none":
                break;
            default:
                System.out.println("Command does not exists");
        }

        return true;
    }

    private void accountCreator() {
        String line = null;
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        try {
            System.out.println("Welcome to account creator");
            System.out.print("Write your first name: ");
            System.out.flush();
            String firstname = in.readLine();

            System.out.print("Write your last name: ");
            System.out.flush();
            String lastname = in.readLine();

            System.out.print("Write your PESEL number: ");
            System.out.flush();
            String peselNumber = in.readLine();

            System.out.print("Write your monthly income: ");
            System.out.flush();
            String monthlyIncome = in.readLine();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
