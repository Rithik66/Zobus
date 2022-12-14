import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Zobus{
    
    static int currentUser,userWallet;
    static String currentAdmin;
    
    static Connection connect;
    static ArrayList<User> tempUsers = new ArrayList<>();
    // static ArrayList<String> nameL = new ArrayList<>();
    // static ArrayList<String> genderL = new ArrayList<>();
    // static ArrayList<Integer> ageL = new ArrayList<>();
    // static Set<Integer> seatL = new HashSet<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/demozobus","root","Rithik66@mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        flush();
        //System.out.println(connect);
        home();
    }

    public static void home() {
        System.out.println("Welcome to Zobus\n");
        System.out.println("1. User");
        System.out.println("2. Admin");
        System.out.println("3. Exit");
        System.out.print("\nEnter your choice : ");
        int choice = scanner.nextInt();
        flush();
        switch(choice){
            case 1: 
                userhome();
                break;
            case 2:
                scanner.nextLine();
                adminhome();
                break;
            case 3:
                System.out.println("See you again !!!");
                hold(3);
                System.exit(1);
            default:
                System.out.println("Wrong choice !");
                hold(1);
                flush();
                break;
        }
        home();
    }

    public static void userhome() {
        System.out.println("1. UserLogin");
        System.out.println("2. UserSignUp");
        System.out.println("3. Back");

        System.out.print("\nEnter your choice : ");
        int choice = scanner.nextInt();
        flush();
        switch(choice){
            case 1: 
                userLogin();
                break;
            case 2:
                scanner.nextLine();
                userSignup();
                break;
            case 3:
                home();
                break;
            default:
                System.out.println("Wrong choice !");
                hold(1);
                flush();
                break;
        }
        userhome();
    }

    public static void userLogin() {
        scanner.nextLine();
        System.out.print("Enter UserName : ");
        String name = scanner.nextLine();
        System.out.print("Enter Password : ");
        String pass = scanner.nextLine();
        //dot("Validating",4);
        flush();
        User u = new User(name, pass.hashCode()+"");
        if(u.validateUser(connect)){
            flush();
            currentUser=u.getId();
            userWallet=u.getWallet();
            userMain();
        }
        else{
            System.out.println("Invalid Credentials! Please try again");
            hold(2);
            flush();
        }
    }

    public static void userSignup() {
        System.out.print("Enter UserName : ");
        String name = scanner.nextLine();
        System.out.print("Enter Password : ");
        String pass = scanner.nextLine();
        System.out.print("Confirm Password : ");
        String conPass = scanner.nextLine();
        System.out.print("Enter Age : ");
        String age = scanner.nextLine();
        System.out.print("Enter Gender (M or F) : ");
        String gender = scanner.nextLine();
        if(pass.equals(conPass)){
            validateSignup(name,pass,Integer.parseInt(age),(gender.charAt(0)+"").toUpperCase());
        }else System.out.println("Confirm password does not match Password");
        //hold(2);
        flush();
        userSignup();
    }

    public static void validateSignup(String name,String pass,int age,String gender){
        try{
            String query="select Id from userlogin where Name = ?";
            //Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            int check=0;
            while(resultSet.next()){
                check=resultSet.getInt(1);
            }
            if(check!=0){
                System.out.println("Please try a new Username");
                hold(2);
                //dot("Redirecting",4);
                flush();
                userSignup();
            }
            else if(pass.length()<8 || pass.length()>24){
                System.out.println("\nA password must be at least 8 characters long and no more than 24 characters long.");
                //dot("\nRedirecting",4);
                hold(6);
                flush();
                //hold(1);
                userSignup();
            }
            else if(age<0){
                System.out.println("Invalid Age format");
                hold(2);
                //dot("Redirecting",4);
                flush();
                userSignup();
            }
            else if(!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")){
                System.out.println("Invalid Gender format");
                hold(2);
                //dot("Redirecting",4);
                flush();
                userSignup();
            }
            else{
                String q = "INSERT INTO userlogin (Name, Password, Age, Gender, wallet) VALUES (?, ?, ?, ?, 0);";
                PreparedStatement preparedStatement = connect.prepareStatement(q);
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,pass.hashCode()+"");
                preparedStatement.setInt(3,age);
                preparedStatement.setString(4,gender.toUpperCase().charAt(0)+"");
                preparedStatement.executeUpdate();
                System.out.println("Account created successfully! Please Login");
                userhome();
            }
        }catch(Exception e){e.printStackTrace();
            System.exit(1);
        }
    }

    public static void userMain() {
        tempUsers.clear();
        System.out.println("1. Ticket Booking");
        System.out.println("2. Cancellation");
        System.out.println("3. View Availability");
        System.out.println("4. View Tickets");
        System.out.println("5. Wallet");
        System.out.println("6. Logout");

        System.out.print("\nEnter your choice : ");
        int choice = scanner.nextInt();
        flush();
        switch(choice){
            case 1: 
                bookTicket();
                break;
            case 2:
                scanner.nextLine();
                cancelTicket();
                break;
            case 3:
                viewAvail();
                break;
            case 4:
                viewTickets();
                break;
            case 5:
                viewWallet();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("Wrong choice !");
                //hold(2);
                flush();
                break;
        }
        userMain();
    }

    public static void viewTickets() {
        try {
            String query="select * from ticket where bookedby = ? ";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1,currentUser);
            ResultSet resultSet = statement.executeQuery();
            int j=1;
            while(resultSet.next()){
                System.out.println("-------------------------");
                System.out.println("TICKET "+j);
                System.out.println("-------------------------");
                String ticketid = resultSet.getString(1);
                System.out.println("Ticked Id : "+ticketid);
                System.out.println("Seats : "+resultSet.getString(3));
                System.out.println("Price : "+resultSet.getString(4));
                j++;
            }

            scanner.nextLine();
            System.out.println("Press Enter to continue");
            scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(){
        currentUser=0;
        userWallet=0;
        dot("Signing off",3);
        flush();
        home();
    }

    public static void bookTicket() {
        System.out.println("Available seats : \n");
        try {
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int i=0;
            while(resultSetq.next()){
                String s=resultSetq.getString(1);

                String query="select count(*) from table where avail = 1 and bus = 'temp'";
                String str = s.replaceAll("\\d", "");
                query=query.replace("table",str);
                query=query.replace("temp",s);
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                int count=0;
                while(resultSet.next()){
                    count=resultSet.getInt(1);
                }
                System.out.println((i+1)+". "+s+":  "+count+" seat(s) available");
                i++;
            }
            if(i==0){
                System.out.println("Sorry no bus available");
                hold(2);
                flush();
                userMain();
            }

            System.out.print("\nEnter a choice : ");
            int a = scanner.nextInt();
            flush();
            printTable(a-1);

        } catch (Exception e) {
            //System.out.println("Please contact admin");
            e.printStackTrace();
        }
    }

    public static void printTable(int i) {
        try {
            int avail=0;
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();
            // System.out.println(i);
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int ind=0;
            String table="";
            A:while(resultSetq.next()){
                table=resultSetq.getString(1);
                if(i==ind){
                    break A;
                }
                ind++;
            }
            // System.out.println(table);
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            // hold(2);
            String query="select * from table where bus = 'temp'";
            String str = table.replaceAll("\\d", "");
            query=query.replace("table",str);
            query=query.replace("temp",table);
            PreparedStatement statement = connect.prepareStatement(query);
            //System.out.println(table);
            ResultSet resultSet = statement.executeQuery();
            String queryq="select totalseats from bus where name = ?";
            PreparedStatement statementq1 = connect.prepareStatement(queryq);
            statementq1.setString(1, table);
            ResultSet resultSetq1 = statementq1.executeQuery();
            resultSetq1.next();
            int tot=resultSetq1.getInt(1);
            int tot4=0,tot6=0;
            tot4=tot%4==0?tot:4-(tot%4)+tot;
            tot6=tot%6==0?tot:6-(tot%6)+tot;
            //System.out.println(tot);
            int n = tot4/4;
            int m = tot6/6;
            if(table.toLowerCase().startsWith("acseater") || table.toLowerCase().startsWith("nonacseater")){
                int count=0,j=1;
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    //System.out.println(seat+" "+bus);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(tempavail==1){
                        if(count<=9) s="0"+count+" | Available";
                        else s=count+" | Available";
                        avail++;
                    }
                    else if(tempavail==-1){
                        if(count<=9) s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9) s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }
                    if(count==j){
                        j+=n;
                        System.out.print("| ");
                    }
                    System.out.printf("%-14s | ",s);
                    if(count==tot4/2) {
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-\n");
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    if(count%n==0 && count!=tot4 && count!=tot4/2){
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
            else{
                int count=0;
                System.out.println("LOWER DECK");
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(tempavail==1){
                        if(count<=9)
                            s="0"+count+" | Available";
                        else s=count+" | Available";
                        avail++;
                    }
                    else if(tempavail==-1){
                        if(count<=9)
                            s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9)
                            s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }

                    if(count%m==1) System.out.print("| ");
                    System.out.printf("%-14s | ",s);
                    if(count==(m*2) || count==((m*2)+(m*3)) || count==tot6/2) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                        if(count==tot6/2) System.out.print("\nUPPER DECK");
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    //System.out.println(m+" "+count+" "+tot);
                    if(count%m==0 && count!=1 && count!=(m*2) && count!=((m*2)+(m*3)) && count!=tot6/2 && count!=tot6) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
            System.out.print("\nEnter no. of seat(s) : ");
            scanner.nextLine();
            int a = scanner.nextInt();

            if(avail<a){
                System.out.println("Enter no of seats correctly");
                hold(2);
            }
            else{
                String query1="select * from table where avail = 1 and bus = 'temp'";
                String st = table.replaceAll("\\d", "");
                query1=query1.replace("table",st);
                query1=query1.replace("temp",table);
                PreparedStatement newstatement = connect.prepareStatement(query1);
                ResultSet resultSet1 = newstatement.executeQuery();
                String seatsavail = "";
                while(resultSet1.next()){
                    seatsavail += resultSet1.isLast()?resultSet1.getInt(1):resultSet1.getInt(1)+",";
                }

                System.out.println("\nAvailable seats : "+seatsavail);

                String passengerseat="";
                for(int j=0;j<a;j++){
                    System.out.println("---------------------\nPassesnger "+(j+1)+"\n---------------------");
                    scanner.nextLine();
                    System.out.print("Enter name : ");
                    String name = scanner.nextLine();
                    System.out.print("Enter gender (M/F): ");
                    String gender = scanner.nextLine();
                    while(!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")){
                        System.out.print("Enter the gender correctly\nM/F : ");
                        gender = scanner.nextLine();
                    }
                    System.out.print("Enter age : ");
                    int age = scanner.nextInt();
                    System.out.print("Enter seat no : ");
                    int seat = scanner.nextInt();
                    if(seatAvail(seat,table,gender)){
                        tempUsers.add(new User(name,(gender.charAt(0)+"").toUpperCase(),seat,age));
                    }
                    else{
                        do{
                            System.out.println("Seat ("+seat+") is not Available. TRY A DIFFERENT SEAT");
                            System.out.print("Enter seat no : ");
                            seat = scanner.nextInt();
                        }while(!seatAvail(seat,table,gender));
                        tempUsers.add(new User(name,(gender.charAt(0)+"").toUpperCase(),seat,age));
                    }
                    passengerseat+=seat+",";
                    
                }
                scanner.nextLine();
                int price = 0;
                System.out.println("---------------------\nConfirm (Y/N)");
                String  s = scanner.nextLine();
                if((s.charAt(0)+"").equalsIgnoreCase("Y")){
                    dot("Generating Bill", 3);
                    String tablesq2 = "select price from bus where name = ?";
                    PreparedStatement statementq2 = connect.prepareStatement(tablesq2);
                    statementq2.setString(1,table);
                    ResultSet resultSetq3 = statementq2.executeQuery();
                    while(resultSetq3.next()){
                        price=a*resultSetq3.getInt(1);
                    }
                    flush();
                    System.out.println("Bill\n---------------------");
                    String ticketid="";
                    ticketid+=currentUser+"_"+table+"_";
                    for(User u : tempUsers){
                        ticketid+=u.getSeat()+",";
                    }
                    ticketid = ticketid.substring(0,ticketid.length()-1);
                    hold(2);
                    generateTickedId(i,passengerseat,currentUser,price,ticketid);
                    hold(1);
                    System.out.println("\n---------------------");
                    System.out.print("\nConfirm Payment(Y/N):");
                    String y = "";
                    do{
                        y = scanner.nextLine();
                    }while(y.equals(""));
                    if((y.charAt(0)+"").equalsIgnoreCase("y")){
                        if(userWallet<price){
                            System.out.println("You dont have enough money in wallet! (if not try relogin)");
                            hold(2);
                        }else{
                            String query3="insert into ticket (ticketid, bookedby, seats, price) VALUES (?, ?, ?, ?);";
                            PreparedStatement statement3 = connect.prepareStatement(query3);
                            statement3.setString(1,ticketid);
                            statement3.setInt(2, currentUser);
                            statement3.setString(3,passengerseat.substring(0,passengerseat.length()-1));
                            statement3.setInt(4, price);
                            statement3.executeUpdate();
                            userWallet-=price;
                            
                            String query2 = "update userlogin set wallet = ? where Id = ?";
                            PreparedStatement statement1 = connect.prepareStatement(query2);
                            statement1.setInt(1,userWallet);
                            statement1.setInt(2,currentUser);
                            statement1.executeUpdate();
                            fillseats(ticketid,i);
                            System.out.println("Payment successfull");
                            System.out.println("!!! THANK YOU FOR CHOOSING ZOBUS !!!");
                            hold(2);
                            scanner.nextLine();
                        }
                    }
                }
                else{
                    System.out.println("Canceled");
                    hold(1);
                    flush();
                }
            }
            // flush();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fillseats(String ticketid,int i) {
        try {
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int ind=0;
            String table="";
            A:while(resultSetq.next()){
                table=resultSetq.getString(1);
                if(i==ind){
                    break A;
                }
                ind++;
            }
            for(int j=0;j<tempUsers.size();j++){
                // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
                String query3="INSERT INTO bookings (seat,bus,name,gender,age,ticketid) VALUES (?,?,?,?,?,?);";
                int seat=tempUsers.get(j).getSeat();
                // System.out.println(tables[i]);
                PreparedStatement statement3 = connect.prepareStatement(query3);
                statement3.setInt(1,seat);
                statement3.setString(2,table);
                statement3.setString(3,tempUsers.get(j).getName());
                statement3.setString(4, tempUsers.get(j).getGender());
                statement3.setInt(5,tempUsers.get(j).getAge());
                statement3.setString(6,ticketid);
                statement3.executeUpdate();

                String query2 = "update table set avail = 0 where seat = ? and bus = 'temp'";
                String st3 = table.replaceAll("\\d", "");
                query2=query2.replace("table",st3);
                query2=query2.replace("temp", table);
                PreparedStatement statement1 = connect.prepareStatement(query2);
                statement1.setInt(1,seat);
                statement1.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateTickedId(int i, String seats,int currentUser,int price,String tickedid) {
        
        try{
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            System.out.println("Ticket Id : "+tickedid);
            String query = "select Name from userlogin where Id = ? ;";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1,currentUser);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            System.out.println("Booked by : "+resultSet.getString(1));
            System.out.println("Seats booked : "+seats.substring(0,seats.length()-1));
            System.out.println("Price : "+price);

        }catch(Exception e){
            e.printStackTrace();
            scanner.nextLine();
            scanner.nextLine();
        }
    }

    public static boolean seatAvail(int seat,String table,String gender){
        try{
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            String query="select * from table where seat = ? and bus = 'temp';";
            String st = table.replaceAll("\\d", "");
            query=query.replace("table",st);
            query=query.replace("temp",table);
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1,seat);
            ResultSet resultSet = statement.executeQuery();
            String check="";
            while(resultSet.next()){
                if(resultSet.getInt(3)==-1) return false;
                String qy = "select * from bookings where seat = ? and bus = ?";
                PreparedStatement pr = connect.prepareStatement(qy);
                pr.setInt(1, seat);
                pr.setString(2, table);
                ResultSet resset = pr.executeQuery();
                while(resset.next()){
                    check = resset.getString(4);
                }
            }
            for(User u: tempUsers){
                if(u.getSeat()==seat){
                    return false;
                }
            }
            if(check!=null && !check.equals("") && (check.equalsIgnoreCase("M") || check.equalsIgnoreCase("F") || check.equalsIgnoreCase("No Seats"))){
                return false;
            }
            PreparedStatement preparedStatement = connect.prepareStatement("select totalseats from bus where name = ?");
            preparedStatement.setString(1,table);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int tot = rs.getInt(1);
            if(table.startsWith("acseater") || table.startsWith("nonacseater")){
                // System.out.println(tot);
                int tot4=tot%4==0?tot:4-(tot%4)+tot;
                int n=tot4/4;
                // System.out.println(n);
                int nearSeat = seat>tot/2?seat+n:seat-n;
                nearSeat = nearSeat<0?nearSeat+(2*n):nearSeat;
                nearSeat = nearSeat>tot?nearSeat-(2*n):nearSeat;
                // System.out.println(seat+" "+nearSeat+" ");
                if((seat<tot4/2 && nearSeat<tot4/2) || (seat>tot4/2 && nearSeat>tot4/2)){
                    // System.out.println(1);
                    String query1="select * from table where seat = ? and bus = 'temp'";
                    String str = table.replaceAll("\\d", "");
                    query1=query1.replace("table",str);
                    query1=query1.replace("temp",table);
                    // System.out.println(table);
                    PreparedStatement statement1 = connect.prepareStatement(query1);
                    statement1.setInt(1, nearSeat);
                    ResultSet resultSet1 = statement1.executeQuery();
                    String nearGender = "";
                    while(resultSet1.next()){
                        int seat2 = resultSet1.getInt(1);
                        String bus1 = resultSet1.getString(2);
                        String qy1 = "select * from bookings where seat = ? and bus = ?";
                        PreparedStatement pr1 = connect.prepareStatement(qy1);
                        pr1.setInt(1, seat2);
                        pr1.setString(2, bus1);
                        ResultSet resset1 = pr1.executeQuery();
                        while(resset1.next()){
                            nearGender = resset1.getString(4);
                        }
                    }
                    // System.out.println(gender+" "+nearGender);
                    if(nearGender==null || nearGender.equals("")){
                        return true;
                    }
                    if(nearGender.equals("No Seat")){
                        return false;
                    }
                    if(nearGender!=null && !gender.equalsIgnoreCase(nearGender)){
                        return false;
                    }
                }
            }else{
                int tot6=tot%6==0?tot:6-(tot%6)+tot;
                int n=tot6/6;
                int nearSeat = seat>=(tot/2)-n?seat+n:seat-n;
                nearSeat = nearSeat<0?nearSeat+(2*n):nearSeat;
                nearSeat = nearSeat>tot?nearSeat-(2*n):nearSeat;
                //System.out.println(seat+" "+nearSeat+" ");
                if((seat<=(tot/2)-n && nearSeat<=(tot/2)-n) || ((seat>tot/2 && nearSeat>tot/2) && (seat<=tot-n && nearSeat<=tot-n))){
                    //System.out.println(1);
                    String query1="select * from table where seat = ? and bus = 'temp'";
                    String str = table.replaceAll("\\d", "");
                    query1=query1.replace("table",str);
                    query1=query1.replace("temp",table);
                    // System.out.println(table);
                    PreparedStatement statement1 = connect.prepareStatement(query1);
                    statement1.setInt(1, nearSeat);
                    ResultSet resultSet1 = statement1.executeQuery();
                    String nearGender = "";
                    while(resultSet1.next()){
                        int seat2 = resultSet1.getInt(1);
                        String bus1 = resultSet1.getString(2);
                        String qy1 = "select * from bookings where seat = ? and bus = ?";
                        PreparedStatement pr1 = connect.prepareStatement(qy1);
                        pr1.setInt(1, seat2);
                        pr1.setString(2, bus1);
                        ResultSet resset1 = pr1.executeQuery();
                        while(resset1.next()){
                            nearGender = resset1.getString(4);
                        }
                    }
                    //System.out.println(gender+" "+nearGender);
                    if(nearGender==null || nearGender.equals("")){
                        return true;
                    }
                    if(nearGender!=null && !gender.equalsIgnoreCase(nearGender)){
                        return false;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Please Contact admin");
        }
        return true;
    }

    public static void cancelTicket() {
        try{
            String query="select * from ticket where bookedby = ? ";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1,currentUser);
            ResultSet resultSet = statement.executeQuery();
            int j=1;
            ArrayList<String> temp = new ArrayList<>();
            while(resultSet.next()){
                System.out.println("-------------------------");
                System.out.println("TICKET "+j);
                System.out.println("-------------------------");
                String ticketid = resultSet.getString(1);
                System.out.println("Ticked Id : "+ticketid);
                temp.add(ticketid);
                System.out.println("Seats : "+resultSet.getString(3));
                System.out.println("Price : "+resultSet.getString(4));
                j++;
            }
            if(j==1){
                System.out.println("No Seats to cancel");
                hold(2);
                userMain();
            }
            System.out.print("-------------------------\nSelect a Ticket : ");
            int select = scanner.nextInt();

            String tid = temp.get(select-1);
            //System.out.println(tid);
            String query2 = "select * from ticket where ticketid = ?";
            PreparedStatement statement2 = connect.prepareStatement(query2);
            statement2.setString(1,tid);
            ResultSet rSet = statement2.executeQuery();
            String arr[] = tid.split("[_]");
            double pri=0;
            String seats = "";
            while(rSet.next()){
                pri=rSet.getInt(4);
                seats=rSet.getString(3);
                //System.out.println(seats);
            }
            //System.out.println(pri);
            String seat[] = seats.split(",");
            double rec=0;
            rec=arr[1].charAt(0)=='a'?(pri/(double)2):(pri/(double)4);
            pri-=rec;
            System.out.println("You will be charged Rs."+rec+" while returning");
            System.out.print("Confirm(Y/N) : ");
            scanner.nextLine();
            String conf = scanner.nextLine();
            conf=conf.charAt(0)+"";
            if(conf.equalsIgnoreCase("y")){
                update(rec,pri,arr[1],seat);
                //System.out.println(pri);
                String query1 = "delete from ticket where ticketid = ?";
                PreparedStatement statement1 = connect.prepareStatement(query1);
                statement1.setString(1,tid);
                statement1.executeUpdate();

                for(String s:seat){
                    String query3 = "update table set `avail` = '1' where `seat` = ? and bus = 'temp';";
                    String st = arr[1].replaceAll("\\d", "");
                    query3=query3.replace("table",st);
                    query3=query3.replace("temp", arr[1]);
                    PreparedStatement statement3 = connect.prepareStatement(query3);
                    statement3.setInt(1,Integer.parseInt(s));
                    statement3.executeUpdate();

                    query3 = "delete from bookings where `seat` = ? and bus = 'temp';";
                    st = arr[1].replaceAll("\\d", "");
                    query3=query3.replace("table",st);
                    query3=query3.replace("temp", arr[1]);
                    statement3 = connect.prepareStatement(query3);
                    statement3.setInt(1,Integer.parseInt(s));
                    statement3.executeUpdate();
                }
                System.out.println("Ticket cancelled successfully!");
                hold(3);
                flush();
            }
            else{
                System.out.println("Denied");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void update(double rec,double ret,String table,String seat[]) {
        userWallet+=ret;
        try {
            String query1 = "update userlogin set wallet = ? where Id = ?";
            PreparedStatement statement1 = connect.prepareStatement(query1);
            statement1.setDouble(1,userWallet);
            statement1.setInt(2, currentUser);
            statement1.executeUpdate();

            for(String s:seat){
                int count=0;
                String query4 = "select canc from table where seat = ? and bus = 'temp'";
                String st = table.replaceAll("\\d", "");
                query4=query4.replace("table",st);
                query4 = query4.replace("temp",table);
                PreparedStatement statement4 = connect.prepareStatement(query4);
                statement4.setInt(1,Integer.parseInt(s));
                ResultSet res = statement4.executeQuery();
                while(res.next()) {
                    count=res.getInt(1);
                }
                count++;
                String query5 = "update table set canc = ? where seat = ? and bus = 'temp'";
                String str = table.replaceAll("\\d", "");
                query5=query5.replace("table",str);
                query5 = query5.replace("temp",table);
                PreparedStatement statement5 = connect.prepareStatement(query5);
                statement5.setInt(1,count);
                statement5.setInt(2,Integer.parseInt(s));
                statement5.executeUpdate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewAvail() {
        ArrayList<String> bus = new ArrayList<>();
        ArrayList<Integer> avail = new ArrayList<>();
        System.out.println("Available seats : \n");

        try {
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            A:while(resultSetq.next()){
                String s=resultSetq.getString(1);

                String query="select count(*) from table where avail = 1 and bus = 'temp'";
                String st = s.replaceAll("\\d", "");
                query=query.replace("table",st);
                query=query.replace("temp",s);
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                int count=0;
                while(resultSet.next()){
                    count=resultSet.getInt(1);
                    if(count==0) continue A;
                    bus.add(s);
                    avail.add(count);
                }
            }

        } catch (Exception e) {
            //System.out.println("Please contact admin");
            e.printStackTrace();
        }
        for(int i=0;i<bus.size();i++){
            for(int j=i+1;j<bus.size();j++){
                if(avail.get(i)<avail.get(j)){
                    int temp1=avail.get(i);
                    avail.set(i, avail.get(j));
                    avail.set(j,temp1);
                    String temp2 = bus.get(i);
                    bus.set(i,bus.get(j));
                    bus.set(j,temp2);
                }
                else if(avail.get(i)==avail.get(j)){
                    if(bus.get(i).contains("nonac") && bus.get(j).contains("ac")){
                        int temp1=avail.get(i);
                        avail.set(i, avail.get(j));
                        avail.set(j,temp1);
                        String temp2 = bus.get(i);
                        bus.set(i,bus.get(j));
                        bus.set(j,temp2);
                    }
                    else if((bus.get(i).contains("nonac") && bus.get(j).contains("nonac"))||(bus.get(i).contains("ac") && bus.get(j).contains("ac"))){
                        if(bus.get(i).contains("seater") && bus.get(j).contains("sleeper")){
                            int temp1=avail.get(i);
                            avail.set(i, avail.get(j));
                            avail.set(j,temp1);
                            String temp2 = bus.get(i);
                            bus.set(i,bus.get(j));
                            bus.set(j,temp2);
                        }
                    }
                }
            }
        }
        for(int i=0;i<bus.size();i++){
            System.out.println(bus.get(i)+": "+avail.get(i)+" seats");
        }
        System.out.println("\nPress enter to continue");
        scanner.nextLine();
        scanner.nextLine();
    }

    public static void viewWallet(){
        System.out.println("1. View Balance");
        System.out.println("2. Update Balance");
        System.out.println("3. Back");
        System.out.print("\nEnter a choice : ");
        int a = scanner.nextInt();
        switch(a){
            case 1:
                System.out.println(userWallet);
                break;
            case 2:
                try{
                    System.out.print("Enter a amount to add : ");
                    double amount = scanner.nextDouble();
                    userWallet+=amount;
                    System.out.println("Amount added!");
                    String query2 = "update userlogin set wallet = ? where Id = ?";
                    PreparedStatement statement1 = connect.prepareStatement(query2);
                    statement1.setInt(1,userWallet);
                    statement1.setInt(2,currentUser);
                    statement1.executeUpdate();
                }catch(Exception e){e.printStackTrace();}
                break;
            case 3:
                userMain();
                break;
        }
        System.out.println("\nPress Enter to continue");
        scanner.nextLine();
        scanner.nextLine();
        viewWallet();
    }


    public static void adminhome(){
        System.out.print("Enter username : ");
        String name = scanner.nextLine();
        System.out.print("Enter password : ");
        String pass = scanner.nextLine();
        //dot("Validating",4);
        flush();
        int check = new User(name,pass).validateAdmin(connect);
        if(check==1){
            System.out.println("Logged in as ADMIN");
            currentAdmin=name;
            hold(2);
            flush();
            adminMain();
        }
        else if(check==2){
            System.out.println("Logged in as SUPER_ADMIN");
            currentAdmin=name;
            hold(2);
            flush();
            superAdminMain();
        }
        else{
            System.out.println("Please check your Credentials");
            hold(2);
            //dot("Redirecting", 4);
            flush();
        }
    }

    private static void adminMain() {
        System.out.println("1. View all Buses");
        System.out.println("2. Add Bus");
        System.out.println("3. Remove Bus");
        System.out.println("4. Logout");
        System.out.print("Enter a choice : ");
        int a = scanner.nextInt();
        flush();
        switch(a){
            case 1:
                viewBus();
                break;
            case 2:
                createBus();
                break;
            case 3:
                deleteBus();
                break;
            case 4:
                logout();
                break;
        }
        adminMain();
    }

    private static void deleteBus() {
        try {
            String getbus = "select name from bus where owner = ?";
            PreparedStatement s = connect.prepareStatement(getbus);
            s.setString(1,currentAdmin);
            ResultSet rs = s.executeQuery();
            String bus = "";
            int i=0;
            while(rs.next()) {
                i++;
                bus = rs.getString(1);
                System.out.println(i+". "+bus);
            }
            if(bus.equals("") || bus==null ){
                System.out.println("No bus to see !!");
                adminMain();
            }
            else System.out.println("Select a bus to delete");
            scanner.nextLine();
            int j = scanner.nextInt();
            delete(j-1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delete(int i) {
        try {
            String tablesq = "select * from bus where owner = ?";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            statementq.setString(1,currentAdmin);
            ResultSet resultSetq = statementq.executeQuery();

            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int ind=0;
            String table="";
            A:while(resultSetq.next()){
                table=resultSetq.getString(1);
                if(i==ind){
                    break A;
                }
                ind++;
            }

            tablesq = "select * from table where avail = 0 and bus = 'temp'";
            String str = table.replaceAll("\\d", "");
            tablesq = tablesq.replace("table", str);
            tablesq = tablesq.replace("temp", table);
            statementq = connect.prepareStatement(tablesq);
            resultSetq = statementq.executeQuery();
            while(resultSetq.next()){
                String ticketid = "";
                int seat2 = resultSetq.getInt(1);
                String bus1 = resultSetq.getString(2);

                String qy1 = "select * from bookings where seat = ? and bus = ?";
                PreparedStatement pr1 = connect.prepareStatement(qy1);
                pr1.setInt(1, seat2);
                pr1.setString(2, bus1);
                ResultSet resset1 = pr1.executeQuery();
                while(resset1.next()){
                    ticketid = resset1.getString(6);
                }
                
                //System.out.println(ticketid);
                String query = "select * from ticket where ticketid = ?";
                PreparedStatement preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1,ticketid);
                ResultSet rs = preparedStatement.executeQuery();
                double price=0;
                int bookedby = 0;
                while(rs.next()){
                    price=rs.getInt(4);
                    bookedby = rs.getInt(2);
                }
                query = "delete from ticket where ticketid = ?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1,ticketid);
                preparedStatement.executeUpdate();

                query = "delete from bookings where seat = ? and bus = ?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1,seat2);
                preparedStatement.setString(2, bus1);
                preparedStatement.executeUpdate();
                //System.out.println(price+" "+bookedby);
                query = "select * from userlogin where Id = ?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setDouble(1,bookedby);
                rs = preparedStatement.executeQuery();
                rs.next();
                double wallet = rs.getDouble(6);
                wallet+=price;
                query = "update userlogin set wallet = ? where Id = ?;";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setDouble(1,wallet);
                preparedStatement.setDouble(2,bookedby);
                preparedStatement.executeUpdate();
            }
            System.out.println("Amount returned to all passengers");

            tablesq = "delete from table where bus = 'temp'";
            String st = table.replaceAll("\\d", "");
            tablesq=tablesq.replace("table",st);
            tablesq=tablesq.replace("temp", table);
            statementq = connect.prepareStatement(tablesq);
            statementq.executeUpdate();

            tablesq = "delete from bus where name = ?";
            statementq = connect.prepareStatement(tablesq);
            statementq.setString(1,table);
            statementq.executeUpdate();
            
            System.out.println("Bus deleted");
            hold(3);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewBus() {
        try {
            String getbus = "select name from bus where owner = ?";
            PreparedStatement s = connect.prepareStatement(getbus);
            s.setString(1,currentAdmin);
            ResultSet rs = s.executeQuery();
            String bus = "";
            int i=0;
            while(rs.next()) {
                i++;
                bus = rs.getString(1);
                System.out.println(i+". "+bus);
            }
            if(bus.equals("") || bus==null ){
                System.out.println("No bus to see !!");
                adminMain();
            }
            else System.out.println("Select a bus to view");
            scanner.nextLine();
            int j = scanner.nextInt();
            printTable1(j-1);
            scanner.nextLine();
            scanner.nextLine();
            flush();
            adminMain();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTable1(int i) {
        try {
            String tablesq = "select * from bus where owner = ?";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            statementq.setString(1,currentAdmin);
            ResultSet resultSetq = statementq.executeQuery();

            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int ind=0;
            String table="";
            A:while(resultSetq.next()){
                table=resultSetq.getString(1);
                if(i==ind){
                    break A;
                }
                ind++;
            }
            //System.out.println(table);
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            String query="select * from table where bus = 'temp'";
            String st = table.replaceAll("\\d", "");
            query=query.replace("table",st);
            query=query.replace("temp",table);
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Bus name : "+table);
            String queryq="select * from bus where name = ?";
            PreparedStatement statementq1 = connect.prepareStatement(queryq);
            statementq1.setString(1, table);
            ResultSet resultSetq1 = statementq1.executeQuery();
            int tot=0;
            double price=0;
            while(resultSetq1.next()){
                tot = resultSetq1.getInt(4);
                price = resultSetq1.getDouble(3);
            }
            System.out.println("Price per seat : "+price);
            System.out.println("Total Seats : "+tot);
            int tot4=0,tot6=0;
            tot4=tot%4==0?tot:4-(tot%4)+tot;
            tot6=tot%6==0?tot:6-(tot%6)+tot;
            //System.out.println(tot);
            int n = tot4/4;
            int m = tot6/6;
            if(table.toLowerCase().startsWith("acseater") || table.toLowerCase().startsWith("nonacseater")){
                int count=0,j=1;
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(tempavail==1){
                        if(count<=9)
                            s="0"+count+" | Available";
                        else s=count+" | Available";
                    }
                    else if(tempavail==-1){
                        if(count<=9)
                            s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9)
                            s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }
                    if(count==j){
                        j+=n;
                        System.out.print("| ");
                    }
                    System.out.printf("%-14s | ",s);
                    if(count==tot4/2) {
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-\n");
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    if(count%n==0 && count!=tot4 && count!=tot4/2){
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
            else{
                int count=0;
                System.out.println("LOWER DECK");
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(gender==null || gender.equals("") || gender.equals("NULL")){
                        if(count<=9)
                            s="0"+count+" | Available";
                        else s=count+" | Available";
                    }
                    else if(tempavail==-1){
                        if(count<=9)
                            s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9)
                            s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }

                    if(count%m==1) System.out.print("| ");
                    System.out.printf("%-14s | ",s);
                    if(count==(m*2) || count==((m*2)+(m*3)) || count==tot6/2) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                        if(count==tot6/2) System.out.print("\nUPPER DECK");
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    //System.out.println(m+" "+count+" "+tot);
                    if(count%m==0 && count!=1 && count!=(m*2) && count!=((m*2)+(m*3)) && count!=tot6/2 && count!=tot6) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTable2(int i) {
        try {
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();

            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int ind=0;
            String table="";
            A:while(resultSetq.next()){
                table=resultSetq.getString(1);
                if(i==ind){
                    break A;
                }
                ind++;
            }
            //System.out.println(table);
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            String query="select * from table where bus = 'temp'";
            String st = table.replaceAll("\\d", "");
            query=query.replace("table",st);
            query=query.replace("temp",table);
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println(table);
            String queryq="select totalseats from bus where name = ?";
            PreparedStatement statementq1 = connect.prepareStatement(queryq);
            statementq1.setString(1, table);
            ResultSet resultSetq1 = statementq1.executeQuery();
            resultSetq1.next();
            int tot=resultSetq1.getInt(1);
            int tot4=0,tot6=0;
            tot4=tot%4==0?tot:4-(tot%4)+tot;
            tot6=tot%6==0?tot:6-(tot%6)+tot;
            //System.out.println(tot);
            int n = tot4/4;
            int m = tot6/6;
            if(table.toLowerCase().startsWith("acseater") || table.toLowerCase().startsWith("nonacseater")){
                int count=0,j=1;
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(tempavail==1){
                        if(count<=9)
                            s="0"+count+" | Available";
                        else s=count+" | Available";
                    }
                    else if(tempavail==-1){
                        if(count<=9)
                            s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9)
                            s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }
                    if(count==j){
                        j+=n;
                        System.out.print("| ");
                    }
                    System.out.printf("%-14s | ",s);
                    if(count==tot4/2) {
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-\n");
                        for(int k=0;k<n;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    if(count%n==0 && count!=tot4 && count!=tot4/2){
                        System.out.println();
                        for(int k=0;k<n;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<n;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
            else{
                int count=0;
                System.out.println("LOWER DECK");
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
                while(resultSet.next()){
                    count++;
                    int seat = resultSet.getInt(1);
                    String bus = resultSet.getString(2);
                    String gender = "";
                    int tempavail = resultSet.getInt(3);
                    String qy = "select * from bookings where seat = ? and bus = ?";
                    PreparedStatement pr = connect.prepareStatement(qy);
                    pr.setInt(1, seat);
                    pr.setString(2, bus);
                    ResultSet resset = pr.executeQuery();
                    while(resset.next()){
                        gender = resset.getString(4);
                    }
                    String s="";
                    if(gender==null || gender.equals("") || gender.equals("NULL")){
                        if(count<=9)
                            s="0"+count+" | Available";
                        else s=count+" | Available";
                    }
                    else if(tempavail==-1){
                        if(count<=9)
                            s="0"+count+" |  No Seat ";
                        else s=count+" |  No Seat ";
                    }
                    else{
                        if(count<=9)
                            s="0"+count+" |     "+gender;
                        else s=count+" |     "+gender;
                    }

                    if(count%m==1) System.out.print("| ");
                    System.out.printf("%-14s | ",s);
                    if(count==(m*2) || count==((m*2)+(m*3)) || count==tot6/2) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                        if(count==tot6/2) System.out.print("\nUPPER DECK");
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("-----------------");
                        }
                        System.out.println("-");
                    }
                    //System.out.println(m+" "+count+" "+tot);
                    if(count%m==0 && count!=1 && count!=(m*2) && count!=((m*2)+(m*3)) && count!=tot6/2 && count!=tot6) {
                        System.out.println();
                        for(int k=0;k<m;k++){
                            System.err.print("+----+-----------");
                        }
                        System.out.println("+");
                    }
                }
                System.out.println();
                for(int k=0;k<m;k++){
                    System.err.print("-----------------");
                }
                System.out.println("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createBus() {
        System.out.println("Select a bus type");
        System.out.println("1. AC Seater\n2. Non-AC Seater\n3. AC Sleeper\n4. Non-AC Sleeper");
        int a = scanner.nextInt();
        String s = a==1?"acseater":a==2?"nonacseater":a==3?"acsleeper":"nonacsleeper";
        try{
            String query = "select count(*) from bus where name like 'temp'";
            query=query.replace("temp", s+"%");
            PreparedStatement preparedStatement = connect.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count=resultSet.getInt(1);
            s+=(count+1);

            hold(1);

            System.out.print("Enter no of seats to add: ");
            int seats = scanner.nextInt();
            query = "INSERT INTO table (seat,bus,avail,canc) VALUES (?, ?, ?, 0);";
            String st = s.replaceAll("\\d", "");
            query=query.replace("table",st);
            int n = s.contains("seater")?4:6;
                int mod=seats%n;
                if(mod!=0){
                    for(int i=1;i<=seats;i++){
                        preparedStatement = connect.prepareStatement(query);
                        preparedStatement.setInt(1,i);
                        preparedStatement.setString(2, s);
                        preparedStatement.setInt(3, 1);
                        preparedStatement.executeUpdate();
                    }
                    for(int i=seats+1;i<=(n-mod)+seats;i++){
                        preparedStatement = connect.prepareStatement(query);
                        preparedStatement.setInt(1,i);
                        preparedStatement.setString(2, s);
                        preparedStatement.setInt(3, -1);
                        preparedStatement.executeUpdate();
                    }
                }
                else{
                    for(int i=1;i<=seats;i++){
                        preparedStatement = connect.prepareStatement(query);
                        preparedStatement.setInt(1,i);
                        preparedStatement.setString(2, s);
                        preparedStatement.setInt(3, 1);
                        preparedStatement.executeUpdate();
                    }
                }
                System.out.print("Enter price per seat : ");
                int price = scanner.nextInt();
                query = "INSERT INTO bus(name,owner,price,totalseats)VALUES(?,?,?,?);";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, s);
                preparedStatement.setString(2, currentAdmin);
                preparedStatement.setInt(3, price);
                preparedStatement.setInt(4,seats);
                preparedStatement.executeUpdate();

                System.out.println("\n\nBus added successfully !!");
                hold(2);
        }catch(Exception e){e.printStackTrace();}
    }

    public static void superAdminMain() {
        System.out.println("1. View Bus");
        System.out.println("2. Add Admin");
        System.out.println("3. Logout");
        System.out.print("Enter a choice : ");
        int a = scanner.nextInt();
        flush();
        switch(a){
            case 1:
                bus();
                break;
            case 2:
                createAdmin();
                break;
            case 3:
                logout();
                break;
        }
        superAdminMain();
    }

    private static void createAdmin() {
        scanner.nextLine();
        System.out.print("Enter name : ");
        String name = scanner.nextLine();
        System.out.print("Password : ");
        String pass = scanner.nextLine();
        System.out.print("Confirm Password : ");
        String confPass = scanner.nextLine();
        User u = new User(name, pass);
        if(confPass.equals(pass)){
            int i=u.validateAdmin(connect);
            if(i==0){
                try {
                    String q = "INSERT INTO adminlogin (name, pass, isadmin) VALUES (?, ?, 1);";
                    PreparedStatement preparedStatement = connect.prepareStatement(q);
                    preparedStatement.setString(1,name);
                    preparedStatement.setString(2,pass);
                    preparedStatement.executeUpdate();
                    System.out.println("Admin added successfully");
                    hold(2);
                    flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Try different admin name");
            }
        }
    }

    public static void bus() {
        ArrayList<String> select = new ArrayList<>();
        try{
            String tablesq = "select * from bus";
            PreparedStatement statementq = connect.prepareStatement(tablesq);
            ResultSet resultSetq = statementq.executeQuery();
            // Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/zobus","root","Rithik66@mysql");
            int i=0;
            while(resultSetq.next()){
                String s = resultSetq.getString(1);
                if(s!=null)select.add(s);
                i++;
                System.out.println(i+". "+s+ " ("+resultSetq.getString(2)+")");
            }
            System.out.print("Enter a choice : ");
            i = scanner.nextInt();
            flush();
            String query = "select count(seat) from table where avail = 0 and bus = 'temp'";
            String st = select.get(i-1).replaceAll("\\d", "");
            query=query.replace("table",st);
            query=query.replace("temp", select.get(i-1));
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            printTable2(i-1);
            System.out.println("\n"+select.get(i-1));
            int seatCount=0;
            while(resultSet.next()) {
                seatCount=resultSet.getInt(1);
            }
            System.out.print("Number of seats filled : "+seatCount);

            String query1 = "select count(seat) from table where canc > 0 and bus = 'temp'";
            String str = select.get(i-1).replaceAll("\\d", "");
            query1=query1.replace("table",str);
            query1=query1.replace("temp", select.get(i-1));
            PreparedStatement statement1 = connect.prepareStatement(query1);
            ResultSet resultSet1 = statement1.executeQuery();
            int canc=0;
            while(resultSet1.next()) {
                canc = resultSet1.getInt(1);
            }
            query1 = "select price from bus where name = ?";
            statement1 = connect.prepareStatement(query1);
            statement1.setString(1,select.get(i-1));
            resultSet1 = statement1.executeQuery();
            int price=0;
            while(resultSet1.next()) {
                price = resultSet1.getInt(1);
            }
            double fare=0;
            if(select.get(i-1).startsWith("ac")){
                fare = canc*((double)price/2);
            }
            System.out.print("\nTotal fair collected : "+((double)(price*seatCount)+fare));

            System.out.println(" ("+seatCount+" tickets"+(canc>0?" + "+canc+" Cancellations)":")"));
            System.out.println("Seat details :");

            String query2 = "select * from table where avail = 0 and bus = 'temp'";
            String str1 = select.get(i-1).replaceAll("\\d", "");
            query2=query2.replace("table",str1);
            query2=query2.replace("temp", select.get(i-1));
            PreparedStatement statement2 = connect.prepareStatement(query2);
            ResultSet resultSet2 = statement2.executeQuery();
            System.out.println("-------------------------------");
            int count=0;
            while(resultSet2.next()){
                int seat2 = resultSet2.getInt(1);
                String bus1 = resultSet2.getString(2);
                System.out.println("Seat no : "+resultSet2.getInt(1));
                String qy1 = "select * from bookings where seat = ? and bus = ?";
                PreparedStatement pr1 = connect.prepareStatement(qy1);
                pr1.setInt(1, seat2);
                pr1.setString(2, bus1);
                ResultSet resset1 = pr1.executeQuery();
                while(resset1.next()){
                    System.out.println("Name : "+resset1.getString(3));
                    System.out.println("Gender : "+resset1.getString(4));
                    System.out.println("Age : "+resset1.getString(5));
                    System.out.println("Ticket-ID : "+resset1.getString(6));
                }
                    
                System.out.println("-------------------------------");
                count++;
            }
            if(count==0) System.out.println("No seats to display\n");
            scanner.nextLine();
            System.out.println("Press Enter to continue");
            scanner.nextLine();
        }catch(Exception e){e.printStackTrace();}
    }

    private static void hold(int sec){
        try {
            Thread.sleep(sec*1000);
        } catch (Exception e) {}
    }

    public static void flush(){
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
    }

    public static void dot(String s,int a) {
        System.out.print(s);
        for(int i=0;i<a;i++){
            try{
                Thread.sleep(500);
            }catch(Exception e){}
            System.out.print(".");
        }
    }
}