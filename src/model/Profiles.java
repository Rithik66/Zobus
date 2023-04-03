package model;

public class Profiles {

    String name;
    String email_id;
    String password;
    int role;
    double wallet;

    public Profiles(){}

    public Profiles(String name, String email_id, String password, long role,double wallet) {
        this.name = name;
        this.email_id = email_id;
        this.password = password;
        this.role = (int)role;
        this.wallet = wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }
}
