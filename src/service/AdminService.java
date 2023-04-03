package service;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.*;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import log.MyLogger;
import model.BusDetails;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;

public class AdminService {

    private static AdminService adminService;

    private AdminService(){}

    public static AdminService getInstance() {
        if(adminService==null){
            adminService = new AdminService();
        }
        return adminService;
    }

    public Response addBus(BusDetails body){
        try{
            DataObject dataObject = new WritableDataObject();
            Row row = new Row("busdetails");
            row.set(2,body.getName());
            row.set(3,body.getOwner());
            row.set(4,body.getPrice());
            row.set(5,body.getTotalseats());
            dataObject.addRow(row);
            DataAccess.add(dataObject);

            int busid =0;
            Criteria criteria = new Criteria(new Column("busdetails","busdetails_name"),body.getName(),QueryConstants.EQUAL);
            DataObject dataObject1 = DataAccess.get("busdetails",criteria);
            Iterator iterator = dataObject1.getRows("busdetails");
            while (iterator.hasNext()){
                Row row1 = (Row) iterator.next();
                busid = row1.getLong(1).intValue();
            }

            DataObject dataObject2 = new WritableDataObject();
            for(int i=0;i< body.getTotalseats();i++){
                Row row2 = new Row("bus");
                row2.set(1,busid);
                row2.set(2,i+1);
                row2.set(3,1);
                dataObject2.addRow(row2);
            }
            DataAccess.add(dataObject2);


        }catch(Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("duplicate",e.getMessage())).build();
        }
        return Response.status(200).entity(body).build();
    }

    public Response addBusForAdmin(BusDetails body,String id){
        try{
            DataObject dataObject = new WritableDataObject();
            Row row = new Row("busdetails");
            row.set(2,body.getName());
            row.set(3,id);
            row.set(4,body.getPrice());
            row.set(5,body.getTotalseats());
            row.set(6,body.getType());
            dataObject.addRow(row);
            DataAccess.add(dataObject);

            long uid = 0;

            DataObject daob = DataAccess.get("busdetails",new Criteria(new Column("busdetails","busdetails_name"),body.getName(),QueryConstants.EQUAL));
            Iterator iterator = daob.getRows("busdetails");
            while(iterator.hasNext()){
                Row row1 = (Row) iterator.next();
                uid=row1.getLong(1);
            }
//            MyLogger.run(uid+"", Level.SEVERE);


            DataObject dataObject2 = new WritableDataObject();
            for(int i=0;i< body.getTotalseats();i++){
                Row row2 = new Row("bus");
                row2.set(1,uid);
                row2.set(2,i+1);
                row2.set(3,1);
                dataObject2.addRow(row2);
            }
            DataAccess.add(dataObject2);

        }catch(Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("duplicate",e.getMessage())).build();
        }
        return Response.status(200).entity(body).build();
    }

    public Response updateBusForAdmin(BusDetails body,String id){
        try{
            int price = 0;
//            MyLogger.run("in update", Level.SEVERE);
            Criteria criteria = new Criteria( new Column("busdetails","busdetails_id"),body.getId(), QueryConstants.EQUAL);

            DataObject dataObject = DataAccess.get("busdetails",criteria);
            Iterator iterator = dataObject.getRows("busdetails");
            while(iterator.hasNext()){
                Row row = (Row)iterator.next();
                price = row.getLong(4).intValue();
                row.set(4,body.getPrice());
                row.set(5,body.getTotalseats());
                row.set(6,body.getType());
                dataObject.updateRow(row);
            }
            DataAccess.update(dataObject);
            body.setPrice(price);
            adminService.refundByBusId(body);

//            MyLogger.run("out update", Level.SEVERE);

        }catch(Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("duplicate",e.getMessage())).build();
        }
        return Response.status(200).entity(body).build();
    }

    public Response getAllBus(){
        ArrayList<BusDetails> busDetailsList = new ArrayList<>();
        BusDetails busDetails = new BusDetails();
        try {
            DataObject dataObject = DataAccess.get("busdetails",(Criteria) null);
            Iterator iterator = dataObject.getRows("busdetails");

//            MyLogger.run("in while", Level.SEVERE);
            while (iterator.hasNext()){
                Row row = (Row) iterator.next();

//                MyLogger.run("in while", Level.SEVERE);
                busDetails = new BusDetails(

                        row.getLong(1).intValue(),
                        row.getString(2),
                        row.getString(3),
                        row.getLong(4).intValue(),
                        row.getLong(5).intValue(),
                        row.getLong(6).intValue()
                );
                busDetailsList.add(busDetails);
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("error",e.getMessage())).build();
        }
        return Response.status(200).entity(busDetailsList).build();
    }

    public Response getAllBusForAdmin(String id){
        ArrayList<BusDetails> busDetailsList = new ArrayList<>();
        BusDetails busDetails = new BusDetails();
        try {

            Table table = new Table("busdetails");
            SelectQuery sq = new SelectQueryImpl(table);

            Column c1 = new Column("busdetails","busdetails_id");
            Column c2 = new Column("busdetails","busdetails_name");
            Column c3 = new Column("busdetails","busdetails_owner");
            Column c4 = new Column("busdetails","busdetails_price");
            Column c5 = new Column("busdetails","busdetails_total_seats");
            Column c6 = new Column("busdetails","busdetails_type");

            sq.setCriteria(new Criteria( new Column("busdetails","busdetails_owner"),id, QueryConstants.EQUAL));

            sq.addSelectColumn(c1);
            sq.addSelectColumn(c2);
            sq.addSelectColumn(c3);
            sq.addSelectColumn(c4);
            sq.addSelectColumn(c5);
            sq.addSelectColumn(c6);

            SortColumn sc = new SortColumn("busdetails","busdetails_id",true);
            sq.addSortColumn(sc);

            RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = relapi.getConnection();
            DataSet ds = relapi.executeQuery(sq,conn);

            while(ds.next()){
//                MyLogger.run(ds.getValue(1)+" "+ ds.getValue(2)+" "+ ds.getValue(3)+" "+ ds.getValue(4)+" "+ ds.getValue(5), Level.SEVERE);
                busDetails = new BusDetails(Integer.parseInt(ds.getValue(1)+""),ds.getValue(2)+"",ds.getValue(3)+"",Integer.parseInt(ds.getValue(4)+""),Integer.parseInt(ds.getValue(5)+""),Integer.parseInt(ds.getValue(6)+""));
                busDetailsList.add(busDetails);
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("error",e.getMessage())).build();
        }
        return Response.status(200).entity(busDetailsList).build();
    }

    public Response validate(String token) {
        if (token==null || token.equals("") || token.isEmpty()){
            return Response.status(Response.Status.UNAUTHORIZED).entity(new Error("status","Login")).build();
        }
        try {
            DataObject dataObject = DataAccess.get("token",(Criteria) null);
            Iterator iterator = dataObject.getRows("token");

//            MyLogger.run("in validate "+token, Level.SEVERE);
            while (iterator.hasNext()){
                Row row = (Row) iterator.next();
//                MyLogger.run("in while "+row.getString(2), Level.SEVERE);
                if( row.getString(2).equals(token)){
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("id",row.getString(1));
//                    MyLogger.run("in if", Level.SEVERE);

                    int role= -1;

                    DataObject do1 = DataAccess.get("profiles",new Criteria(new Column("profiles","email_id"),row.getString(1),QueryConstants.EQUAL));
                    Iterator iterator1 = do1.getRows("profiles");
                    while (iterator1.hasNext()){
                        Row row1 = (Row) iterator1.next();
                        role = ((Long)row1.get("role")).intValue();
                    }

                    Error error = new Error(role+"",row.getString(1));
                    return Response.status(200).entity(error).build();
                }
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
            return Response.status(500).entity(new Error("error",e.getMessage())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public String find(String token){
        try {
            DataObject dataObject = DataAccess.get("token",(Criteria) null);
            Iterator iterator = dataObject.getRows("token");

//            MyLogger.run("in find ", Level.SEVERE);
            while (iterator.hasNext()){
                Row row = (Row) iterator.next();
//                MyLogger.run(row.getString(2)+" : "+token+" : "+row.getString(2).equals(token), Level.SEVERE);
                if( row.getString(2).equals(token)){
//                    MyLogger.run("in find while if "+row.getString(1), Level.SEVERE);
                    return row.getString(1);
                }
            }
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return "";
    }

    public Response deleteBusForAdmin(int busId){
        try{
            BusDetails busDetails = new BusDetails();
            busDetails.setId(busId);

            int price = 0;
            Criteria c = new Criteria(new Column("busdetails","busdetails_id"),busId,QueryConstants.EQUAL);
            DataObject dO = DataAccess.get("busdetails",c);
            Iterator iter = dO.getRows("busdetails");
            while(iter.hasNext()){
                Row row = (Row) iter.next();
                price = row.getLong(4).intValue();
            }
            busDetails.setPrice(price);
            refundByBusId(busDetails);

            Criteria criteria = new Criteria( new Column("busdetails","busdetails_id"),busId, QueryConstants.EQUAL);
            DataAccess.delete(criteria);

            Criteria criteria1 = new Criteria( new Column("bus","bus_id"),busId, QueryConstants.EQUAL);
            DataAccess.delete(criteria1);
//            MyLogger.run("deleted", Level.SEVERE);
            return Response.status(200).build();
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.status(406).build();
    }

    public Response deleteAdmin(String owner){
        try{
            Criteria criteria = new Criteria(new Column("busdetails","busdetails_owner"),owner,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("busdetails",criteria);
            Iterator iterator = dataObject.getRows("busdetails");
            while(iterator.hasNext()){
                Row row = (Row)iterator.next();
                long bus = row.getLong(1);
                Criteria criteria1 = new Criteria(new Column("bus","bus_id"),bus,QueryConstants.EQUAL);
                DataAccess.delete(criteria1);
                BusDetails busDetails = new BusDetails();
                busDetails.setPrice(row.getLong(4).intValue());
                busDetails.setId(row.getLong(1).intValue());
                refundByBusId(busDetails);
            }
            Criteria criteria1 = new Criteria( new Column("profiles","email_id"),owner, QueryConstants.EQUAL);
            DataAccess.delete(criteria1);
            DataAccess.delete(criteria);
//            MyLogger.run("deleted", Level.SEVERE);
            return Response.status(200).build();
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.status(406).build();
    }

    public void refundByBusId(BusDetails body){
        UserService userService = UserService.getInstance();

        int id = body.getId();
        int price = body.getPrice();

        try{
            Criteria criteria = new Criteria(new Column("bookings","bookings_bus_id"),id,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("bookings",criteria);
            Iterator iterator = dataObject.getRows("bookings");
            long ticket = 0;
            while(iterator.hasNext()){
                Row row = (Row) iterator.next();
                ticket = row.getLong(6);
                Criteria criteria1 = new Criteria(new Column("ticket","ticket_id"),ticket,QueryConstants.EQUAL);
                DataObject dataObject1 = DataAccess.get("ticket",criteria1);
                Iterator iterator1 = dataObject1.getRows("ticket");
                while(iterator1.hasNext()){
                    Row row1 = (Row) iterator1.next();
                    userService.addFund(price+"",row1.getString(2));
                }
            }
            Criteria criteria2 = new Criteria(new Column("ticket","ticket_id"),ticket,QueryConstants.EQUAL);
            DataAccess.delete(criteria2);
            DataAccess.delete(criteria);
            Criteria criteria1 = new Criteria(new Column("bus","bus_id"),id,QueryConstants.EQUAL);
            DataObject dataObject1 = DataAccess.get("bus",criteria1);
            Iterator iterator1 = dataObject1.getRows("bus");
            while(iterator1.hasNext()){
                Row row1 = (Row) iterator1.next();
                row1.set(3,1);
                dataObject1.updateRow(row1);
            }
            DataAccess.update(dataObject1);

        }catch(Exception e){
            MyLogger.exceptionLogger(e);
        }
    }

    public Response getBookings(String id){
        ArrayList<Map> arrayList = new ArrayList<>();
        try{
            Table table1 = new Table("bookings");
            SelectQuery sq = new SelectQueryImpl(table1);
            Column col1 = new Column("bookings","bookings_bus_id");
            Column col2 = new Column("busdetails","busdetails_price");
            Column col3 = new Column("bookings","bookings_ticket_id");
            Column col4 = new Column("bookings","bookings_seat");
            Column col5 = new Column("ticket","ticket_bookedby");
            Column col6 = new Column("busdetails","busdetails_name");

            ArrayList colList = new ArrayList();
            colList.add(col1);
            colList.add(col2);
            colList.add(col3);
            colList.add(col4);
            colList.add(col5);
            colList.add(col6);
            sq.addSelectColumns(colList);

            Criteria c1 = new Criteria(new Column("busdetails", "busdetails_owner"),id, QueryConstants.EQUAL);
            sq.setCriteria(c1);

            Join join = new Join("bookings", "busdetails", new String[]{"bookings_bus_id"}, new String[]{"busdetails_id"}, Join.LEFT_JOIN);
            Join join1 = new Join("bookings","ticket", new String[]{"bookings_ticket_id"}, new String[]{"ticket_id"}, Join.LEFT_JOIN);
            sq.addJoin(join);
            sq.addJoin(join1);

            RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = relapi.getConnection();
            DataSet ds = relapi.executeQuery(sq,conn);

            while(ds.next()){
                Map<String,String> map = new HashMap<>();
                map.put("bus",ds.getValue("bookings_bus_id")+"");
                map.put("bus_name",ds.getValue("busdetails_name")+"");
                map.put("seat",ds.getValue("bookings_seat")+"");
                map.put("price",ds.getValue("busdetails_price")+"");
                map.put("ticket",ds.getValue("bookings_ticket_id")+"");
                map.put("booked_by",ds.getValue("ticket_bookedby")+"");
                arrayList.add(map);
            }
            return Response.status(200).entity(arrayList).build();
        }catch(Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.status(500).entity(new Error("No return","End of the Endpoint")).build();
    }
    public Response changePassword(HashMap<String,String> body,String email_id){
        try {
            Criteria criteria = new Criteria(new Column("profiles","email_id"),email_id,QueryConstants.EQUAL);
            DataObject dataObject = DataAccess.get("profiles",criteria);
            Iterator iterator = dataObject.getRows("profiles");
            MyLogger.run("changePassword "+body.get("orgpass"),Level.INFO);
            while(iterator.hasNext()){
                Row row = (Row)iterator.next();
                if(!row.getString(3).equals(body.get("orgpass"))){
                    return Response.status(401).build();
                }else{
                    row.set("password",body.get("newpass"));
                    MyLogger.run(body.get("newpass"),Level.INFO);
                    dataObject.updateRow(row);
                }
            }
            DataAccess.update(dataObject);
        }catch(Exception e){
            MyLogger.exceptionLogger(e);
        }
        return Response.status(200).build();
    }
}
