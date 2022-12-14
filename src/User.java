import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {
    
    private String name,password,gender;
    private int id,wallet,isadmin,age,seat;
    public User(String name, String gender, int seat, int age) {
        this.name = name;
        this.gender = gender;
        this.seat = seat;
        this.age = age;
    }
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getWallet() {
        return wallet;
    }
    public void setWallet(int wallet) {
        this.wallet = wallet;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getIsadmin() {
        return isadmin;
    }
    public void setIsadmin(int isadmin) {
        this.isadmin = isadmin;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getSeat() {
        return seat;
    }
    public void setSeat(int seat) {
        this.seat = seat;
    }
    
    public boolean validateUser(Connection connect){
        try{
            String query="select * from userlogin where Name = ? and Password = ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1,name);
            statement.setString(2,password);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                this.id=resultSet.getInt(1);
                this.wallet=resultSet.getInt(6);
            }
            if(id==0) return false;
        }catch(Exception e){e.printStackTrace();
            return false;
        }
        return true;
    }

    public int validateAdmin(Connection connect){
        try {
            String query="select * from adminlogin where name = ? and pass = ?";
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1,name);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                this.isadmin=resultSet.getInt(3);
            }
            if(isadmin==2) return 2;
            else if(isadmin==1) return 1;
        } catch (Exception e) {e.printStackTrace();}
        return 0;
    }

    
}
