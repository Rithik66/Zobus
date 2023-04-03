package service;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.*;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import log.MyLogger;
import model.Bus;
import model.Profiles;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {

    private static UserService userService;
    public CommonService commonService = new CommonService();
    private UserService(){}

    public static UserService getInstance(){
        if(userService == null){
            userService = new UserService();
        }
        return userService;
    }

    public Response getAllProfile(){
        ArrayList<Profiles> profiles = new ArrayList<>();
        Profiles profile;
        try {
            DataObject dataObject = DataAccess.get("profiles",new Criteria(new Column("profiles","role"),1, QueryConstants.EQUAL));
            Iterator iterator = dataObject.getRows("profiles");

            while (iterator.hasNext()){
                Row row = (Row) iterator.next();
                profile = new Profiles(
                        row.getString(1),
                        row.getString(2),
                        row.getString(3),
                        row.getLong(4),
                        row.getDouble(5)
                );
                profiles.add(profile);
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("error",e.getMessage())).build();
        }
        return Response.status(200).entity(profiles).build();
    }

    public Response validate(Profiles body){
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body.getPassword());
        if(matcher.matches()){
            body.setRole(2);
            return Response.status(200).entity(body).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(body).build();
    }

    public Response createProfile(Profiles body){
        try{
            DataObject dataObject = new WritableDataObject();
            Row row = new Row("profiles");
            row.set(1,body.getName());
            row.set(2,body.getEmail_id());
            row.set(3,body.getPassword());
            row.set(4,body.getRole());
            row.set(5,0);
            dataObject.addRow(row);
            DataAccess.add(dataObject);

            if(body.getRole()==1){
                MyLogger.run(body.getEmail_id(), Level.INFO);
                commonService.sendMail("Admin Profile Created","Dear "+body.getName()+",\nYour Temporary Password is : "+body.getPassword(), body.getEmail_id());

            }
        }catch(Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("duplicate",e.getMessage())).build();
        }
        return Response.status(200).entity(body).build();
    }

    public Response getBusSeats(int busId){
        ArrayList<Bus> allseats = new ArrayList<>();
        try {
//            MyLogger.run("getbusseats: "+busId, Level.SEVERE);
            Table table = new Table("bus");
            SelectQuery sq = new SelectQueryImpl(table);

            Column c1 = new Column("bus","bus_id");
            Column c2 = new Column("bus","bus_seat");
            Column c3 = new Column("bus","bus_seat_avail");

            sq.setCriteria(new Criteria( new Column("bus","bus_id"),busId, QueryConstants.EQUAL));

            sq.addSelectColumn(c1);
            sq.addSelectColumn(c2);
            sq.addSelectColumn(c3);

            SortColumn sc = new SortColumn("bus","bus_seat",true);
            sq.addSortColumn(sc);

            RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = relapi.getConnection();
            DataSet ds = relapi.executeQuery(sq,conn);
            while (ds.next()){
//                MyLogger.run("getbusseats while: "+busId, Level.SEVERE);
                allseats.add(new Bus(ds.getValue(1)+"",ds.getValue(2)+"",Integer.parseInt(ds.getValue(3)+"")));
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("error",e.getMessage())).build();
        }
        return Response.status(200).entity(allseats).build();
    }

    public Response seatCheck(HashMap<String,String> body){
        ArrayList<Map> list = new ArrayList<>();
//        MyLogger.run("check "+body.toString(),Level.INFO);
        try{
//
//            String selected_seats = body.get("selected-seats");
//            String selected_genders = body.get("gender");
//            String selected_age = body.get("age");
//            MyLogger.run(selected_seats+" : "+selected_genders+" : "+selected_age,Level.INFO);
//
            String selected_bus = body.get("bus_id");
            String selected_seats = body.get("selected-seats").substring(0,body.get("selected-seats").length()-1);
            String selected_genders = body.get("gender").substring(0,body.get("gender").length()-1);
            String selected_age = body.get("age").substring(0,body.get("age").length()-1);

//            MyLogger.run(selected_bus+" : "+selected_seats+" : "+selected_genders+" : "+selected_age,Level.INFO);

            StringTokenizer selectedSeats = new StringTokenizer(selected_seats,",");
            int no_of_seats = selectedSeats.countTokens();
            StringTokenizer selectedGenders = new StringTokenizer(selected_genders,",");
            int all_genders = selectedGenders.countTokens();
            StringTokenizer selectedAge = new StringTokenizer(selected_age,",");
            int all_age = selectedAge.countTokens();

            if(no_of_seats!=all_genders || all_genders!=all_age){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }else{
                for(int i=0;i<no_of_seats;i++){
                    Map<String,String> response = new HashMap<>();
                    int seat = Integer.parseInt(selectedSeats.nextToken());
                    String gender = selectedGenders.nextToken();
                    int near_seat = seat%2==0?seat-1:seat+1;
                    int age = Integer.parseInt(selectedAge.nextToken());
//                    MyLogger.run(gender+":"+seat+" = Gender:"+near_seat,Level.INFO);

                    Criteria criteria1 = new Criteria(new Column("bookings","bookings_bus_id"),Integer.parseInt(selected_bus),QueryConstants.EQUAL);
                    Criteria criteria2 = new Criteria(new Column("bookings","bookings_seat"),near_seat,QueryConstants.EQUAL);
                    Criteria criteria = criteria1.and(criteria2);

                    DataObject dataObject = DataAccess.get("bookings",criteria);
                    Iterator iterator = dataObject.getRows("bookings");
                    String near_gender = "";
                    while (iterator.hasNext()){
                        Row row = (Row)iterator.next();
                        near_gender = row.getString(4);
                    }
//                    MyLogger.run(gender+":"+seat+" = "+near_gender+":"+near_seat,Level.INFO);

                    if(near_gender.equals(gender) || near_gender==null || near_gender.equals("")){
                        response.put("status","book");
                    }else{
                        response.put("status","skip");
                    }

                    response.put("seat",seat+"");
                    response.put("bus",selected_bus);
                    response.put("gender",gender);
                    response.put("age",age+"");
                    response.put("name",body.get("name"));

//                    MyLogger.run("seat check "+near_gender+" : ",Level.INFO);

                    long price = 0;
                    Criteria criteria3 = new Criteria(new Column("busdetails","busdetails_id"),Integer.parseInt(selected_bus),QueryConstants.EQUAL);
                    DataObject dataObject1 = DataAccess.get("busdetails",criteria3);
                    Iterator iterator1 = dataObject1.getRows("busdetails");
                    while(iterator1.hasNext()){
                        Row row = (Row)iterator1.next();
                        price = row.getLong(4);
                    }
                    response.put("price",price+"");
                    list.add(response);
                }
            }
        }catch(Exception e){MyLogger.exceptionLogger(e);}
        return Response.ok().entity(list).build();
    }

    public Response seatBook(HashMap<String,String> body,String token,String... tk){
//        MyLogger.run("Book : "+body.toString(),Level.INFO);
        try{
            StringTokenizer seats = new StringTokenizer(body.get("seat"),",");
            StringTokenizer genders = new StringTokenizer(body.get("gender"),",");
            StringTokenizer ages = new StringTokenizer(body.get("age"),",");

            int n = seats.countTokens();

//            MyLogger.run(body.get("seat")+" "+body.get("gender")+" "+body.get("age")+" "+body.get("name")+" "+body.get("bus"),Level.INFO);

            DataObject dataObject = DataAccess.get("token",(Criteria) null);
            Iterator iterator = dataObject.getRows("token");
//            MyLogger.run("Book : token "+token,Level.INFO);
            String decoded = "";
            while (iterator.hasNext()) {
                Row row = (Row) iterator.next();
//                MyLogger.run("Book : "+row.getString(2)+" "+row.getString(1),Level.INFO);
                if (row.getString(2).equals(token)) {
                    decoded = row.getString(1);
//                    MyLogger.run("Book if : "+decoded,Level.INFO);
                }else{
//                    MyLogger.run("Book else",Level.INFO);
                }
            }
            if(decoded.equals("")){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            int bus = Integer.parseInt(body.get("bus"));
            double amount = 0;
//            MyLogger.run("BUS : "+bus+"",Level.INFO);

            Criteria criteria4 = new Criteria(new Column("busdetails","busdetails_id"),bus,QueryConstants.EQUAL);
            DataObject dataObject4 = DataAccess.get("busdetails",criteria4);
            Iterator iterator2 = dataObject4.getRows("busdetails");
            while(iterator2.hasNext()){
                Row row = (Row) iterator2.next();
                amount = row.getLong(4);
//                MyLogger.run("amount 1 : "+amount+"",Level.INFO);
            }
            amount*=n;
//            MyLogger.run("amount 2 : "+amount+"",Level.INFO);

            Response response = removeFund(amount+"",decoded);
            if(response.getStatus()==403){
                return response;
            };

            DataObject dataObject2 = new WritableDataObject();
            Row row2 = new Row("ticket");
            row2.set(2,decoded);
            row2.set(3,"Booked");
            dataObject2.addRow(row2);
            DataAccess.add(dataObject2);

            Table table = new Table("ticket");
            SelectQuery sq = new SelectQueryImpl(table);

            Column c1 = new Column("ticket",1);
            Column c2 = new Column("ticket",2);
            Column c3 = new Column("ticket",3);

            sq.addSelectColumn(c1);
            sq.addSelectColumn(c2);
            sq.addSelectColumn(c3);

            SortColumn sc = new SortColumn("ticket","ticket_id",true);
            sq.addSortColumn(sc);

            RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = relapi.getConnection();
            DataSet ds = relapi.executeQuery(sq,conn);

            int ticket_id = 0;
            while (ds.next()){
                ticket_id = Integer.parseInt(ds.getValue(1)+"");
            }
//            MyLogger.run(ticket_id+"",Level.INFO);

            while (n-->0){

//                MyLogger.run(seats.nextToken()+" : "+genders.nextToken()+" : "+ages.nextToken()+" : "+body.get("bus")+" "+body.get("name"),Level.INFO);
                int seat = Integer.parseInt(seats.nextToken());
                DataObject dataObject1 = new WritableDataObject();
                Row row = new Row("bookings");
                row.set(1,seat);
                row.set(2,bus);
                row.set(3,body.get("name"));
                row.set(4,genders.nextToken());
                row.set(5,Integer.parseInt(ages.nextToken()));
                row.set(6,ticket_id);

                dataObject1.addRow(row);
                DataAccess.add(dataObject1);

                Criteria criteria1 = new Criteria(new Column("bus","bus_id"),bus,QueryConstants.EQUAL);
                Criteria criteria2 = new Criteria(new Column("bus","bus_seat"),seat,QueryConstants.EQUAL);
                Criteria criteria = criteria1.and(criteria2);

                DataObject dataObject3 = DataAccess.get("bus",criteria);
                Iterator iterator1 = dataObject3.getRows("bus");
                while(iterator1.hasNext()){
//                    MyLogger.run("bus while",Level.INFO);
                    Row row1 = (Row) iterator1.next();
//                    MyLogger.run(row1.getLong(1)+"",Level.INFO);
//                    MyLogger.run(row1.getLong(2)+"",Level.INFO);
//                    MyLogger.run(row1.getLong(3)+"",Level.INFO);
                    row1.set(3,0);
                    dataObject3.updateRow(row1);
                }
                DataAccess.update(dataObject3);

            }
        }catch(Exception e){MyLogger.exceptionLogger(e);}
        return Response.accepted().build();
    }

    public Response getCancelDetails(String id){
        ArrayList<Map> list = new ArrayList<>();
        try{
//            MyLogger.run(id+"", Level.SEVERE);
//            MyLogger.run("IN", Level.SEVERE);
            Criteria c1 = new Criteria(new Column("ticket","ticket_bookedby"),id,QueryConstants.EQUAL);
            Criteria c2 = new Criteria(new Column("ticket","ticket_status"),"Booked",QueryConstants.EQUAL);
            Criteria criteria = c1.and(c2);
            DataObject dataObject = DataAccess.get("ticket",criteria);
            Iterator iterator = dataObject.getRows("ticket");
            String all_ticket_id = "";
            while (iterator.hasNext()){
                Row row = (Row)iterator.next();
                all_ticket_id += row.getLong(1)+",";
//                MyLogger.run("Ticket : "+all_ticket_id,Level.INFO);
            }
            all_ticket_id=all_ticket_id.length()>0?all_ticket_id.substring(0,all_ticket_id.length()-1):all_ticket_id;
//            MyLogger.run("all tickets "+all_ticket_id, Level.SEVERE);
            StringTokenizer ati = new StringTokenizer(all_ticket_id,",");
            int n=ati.countTokens();
//            MyLogger.run(n+" inside", Level.SEVERE);
            while(n-->0){
                Map<String,String> map = new HashMap<>();
                long ticket_id = Long.parseLong(ati.nextToken());
                map.put("ticket",ticket_id+"");
                Criteria criteria1 = new Criteria(new Column("bookings","bookings_ticket_id"),ticket_id,QueryConstants.EQUAL);
                DataObject dataObject1 = DataAccess.get("bookings",criteria1);
                Iterator iterator1 = dataObject1.getRows("bookings");
                String seats = "";
                long bus_id = 0;
                while(iterator1.hasNext()){
                    Row row = (Row)iterator1.next();
                    seats+=row.getLong(1)+",";
                    bus_id = row.getLong(2);
                }
                if(seats.length()>1) seats=seats.substring(0,seats.length()-1);
                int noOfSeats = new StringTokenizer(seats,",").countTokens();
                map.put("seats",seats);
                map.put("noOfSeats",noOfSeats+"");
//                3.run(seats+"", Level.SEVERE);

                Criteria criteria2 = new Criteria(new Column("busdetails","busdetails_id"),bus_id,QueryConstants.EQUAL);
                DataObject dataObject2 = DataAccess.get("busdetails",criteria2);
                Iterator iterator2 = dataObject2.getRows("busdetails");
                String bus_name = "";
                long price = 0;
                while(iterator2.hasNext()){
                    Row row = (Row)iterator2.next();
                    bus_name = row.getString(2);
                    price = row.getLong(4);
                }
                map.put("bus",bus_name+"");
                map.put("pricePerSeat",price+"");
                map.put("totalPrice",price*noOfSeats+"");
//                MyLogger.run(bus_name+"", Level.SEVERE);

                list.add(map);
            }
//            MyLogger.run(list+"", Level.SEVERE);
        }
        catch(Exception e){
            MyLogger.exceptionLogger(e);
        }

//        MyLogger.run("OUTSIDE TRY CATCH", Level.SEVERE);
        return Response.ok().entity(list).build();
    }

    public Response cancelTicket(HashMap<String,String> body){
//        MyLogger.run("In cancelTicket "+body.get("tickets"), Level.SEVERE);
        try{
            StringTokenizer stringTokenizer = new StringTokenizer(body.get("tickets"),",");
            int n = stringTokenizer.countTokens();
            while(n-->0){
                long id = Long.parseLong(stringTokenizer.nextToken());
//                MyLogger.run("cancel ticket : "+id,Level.INFO);
                Criteria criteria = new Criteria(new Column("ticket","ticket_id"),id,QueryConstants.EQUAL);
                DataObject dataObject = DataAccess.get("ticket",criteria);
                Iterator iterator = dataObject.getRows("ticket");
                while (iterator.hasNext()){
                    Row row = (Row)iterator.next();
                    row.set(3,"Canceled");
                    dataObject.updateRow(row);
                }
                DataAccess.update(dataObject);

                Criteria criteria1 = new Criteria(new Column("bookings","bookings_ticket_id"),id,QueryConstants.EQUAL);
                DataObject dataObject1 = DataAccess.get("bookings",criteria1);
                Iterator iterator1 = dataObject1.getRows("bookings");
                while(iterator1.hasNext()){
                    Row row = (Row)iterator1.next();
                    long seat = row.getLong(1);
                    long bus = row.getLong(2);
//                    MyLogger.run(seat+" : "+bus, Level.SEVERE);
                    Criteria c1 = new Criteria(new Column("bus","bus_seat"),seat,QueryConstants.EQUAL);
                    Criteria c2 = new Criteria(new Column("bus","bus_id"),bus,QueryConstants.EQUAL);
                    Criteria criteria2 = c1.and(c2);
                    DataObject dataObject2 = DataAccess.get("bus",criteria2);
                    Iterator iterator2 = dataObject2.getRows("bus");
                    while(iterator2.hasNext()){
                        Row row1 = (Row)iterator2.next();
//                        MyLogger.run(row1.getLong(3)+"", Level.SEVERE);
                        row1.set(3,1);
                        dataObject2.updateRow(row1);
                    }
                    DataAccess.update(dataObject2);

                    refundByTicketId(id);

                    Criteria criteria3 = new Criteria( new Column("bookings","bookings_ticket_id"),id, QueryConstants.EQUAL);
                    DataAccess.delete(criteria3);
                }
            }
        }catch(Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.ok().build();
    }

    public double getFund(String id){
        double wallet = 0;
        try{
            DataObject dataObject = DataAccess.get("profiles",new Criteria(new Column("profiles","email_id"),id,QueryConstants.EQUAL));
            Iterator iterator = dataObject.getRows("profiles");
            while(iterator.hasNext()){
                Row row = (Row) iterator.next();
                wallet = row.getDouble(5);
            }
        }
        catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return wallet;
    }

    public double addFund(String amount, String id){
        double wallet = 0;
        try{
//            MyLogger.run("in add fund "+getFund(id)+" : "+amount,Level.INFO);
            Criteria criteria = new Criteria(new Column("profiles","email_id"),id,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("profiles",criteria);
            Iterator iterator = dataObject.getRows("profiles");
            while(iterator.hasNext()){
                Row row = (Row) iterator.next();
                row.set(5,row.getDouble(5)+Double.valueOf(amount));
                dataObject.updateRow(row);
//                MyLogger.run("in add fund while "+getFund(id),Level.INFO);
            }
            DataAccess.update(dataObject);
        }
        catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return wallet;
    }

    public Response removeFund(String amount, String id){
        double wallet = 0;
        try{
//            MyLogger.run("fund : "+getFund(id)+"",Level.INFO);
            if(getFund(id)<Double.valueOf(amount)){
                return Response.status(Response.Status.FORBIDDEN).build();
            }
//            MyLogger.run("in remove fund "+getFund(id)+" : "+amount,Level.INFO);
            Criteria criteria = new Criteria(new Column("profiles","email_id"),id,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("profiles",criteria);
            Iterator iterator = dataObject.getRows("profiles");
            while(iterator.hasNext()){
                Row row = (Row) iterator.next();
                row.set(5,row.getDouble(5)-Double.valueOf(amount));
                dataObject.updateRow(row);
//                MyLogger.run("in remove fund while "+getFund(id),Level.INFO);
            }
            DataAccess.update(dataObject);
        }
        catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.status(Response.Status.ACCEPTED).entity(wallet).build();
    }

    public void refundByTicketId(long ticket_id){
        try{
            String id = "";
            long price = 0;
            Criteria criteria = new Criteria(new Column("ticket","ticket_id"),ticket_id,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("ticket",criteria);
            Iterator iterator = dataObject.getRows("ticket");
//            MyLogger.run("ticket id in refund : "+ticket_id,Level.INFO);
            while (iterator.hasNext()){
                Row row = (Row)iterator.next();
                id = row.getString(2);
//                MyLogger.run("id in refund : "+id,Level.INFO);
            }
            Criteria criteria1 = new Criteria(new Column("bookings","bookings_ticket_id"),ticket_id+"",QueryConstants.EQUAL);
            DataObject dataObject1 = DataAccess.get("bookings",criteria1);
            Iterator iterator1 = dataObject1.getRows("bookings");
            while(iterator1.hasNext()){
                Row row = (Row)iterator1.next();
                long bus = row.getLong(2);
//                MyLogger.run("BUS in refund : "+bus,Level.INFO);
                Criteria  criteria2 = new Criteria(new Column("busdetails","busdetails_id"),bus,QueryConstants.EQUAL);
                DataObject dataObject2 = DataAccess.get("busdetails",criteria2);
                Iterator iterator2 = dataObject2.getRows("busdetails");
                while (iterator2.hasNext()){
                    Row row1 = (Row)iterator2.next();
                    price = row1.getLong(4);
//                    MyLogger.run("Price in refund "+price,Level.INFO);
                }
                addFund(price+"",id);
            }


        }catch(Exception e){
            MyLogger.exceptionLogger(e);
        }
    }

}
