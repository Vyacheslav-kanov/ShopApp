package app.shop.model;

public class User {

    private String userName;
    private String mail;
    private String password;
    private double balance;

    public User(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.balance = 0;
    }

    public User(String userName, String mail, String password, double balance) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.balance = balance;
    }

    public String getUserName() {
        return userName;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
