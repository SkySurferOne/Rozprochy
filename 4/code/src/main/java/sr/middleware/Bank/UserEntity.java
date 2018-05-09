package sr.middleware.Bank;

public class UserEntity {
    private final String firstname;
    private final String lastname;
    private final String peselNumber;
    private final long monthlyIncome;
    private final UserType userType;
    private double accountState = 0;

    public UserEntity(String firstname, String lastname, String peselNumber, long monthlyIncome, UserType userType) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.peselNumber = peselNumber;
        this.monthlyIncome = monthlyIncome;
        this.userType = userType;
    }

    public void setAccountState(double accountState) {
        this.accountState = accountState;
    }

    public double getAccountState() {
        return accountState;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPeselNumber() {
        return peselNumber;
    }

    public long getMonthlyIncome() {
        return monthlyIncome;
    }

    public UserType getUserType() {
        return userType;
    }

    enum UserType {
        STANDARD,
        PREMIUM
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", peselNumber='" + peselNumber + '\'' +
                ", monthlyIncome=" + monthlyIncome +
                ", userType=" + userType.toString() +
                '}';
    }
}
