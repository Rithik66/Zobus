package controller;

import log.MyLogger;
import model.Profiles;
import service.AdminService;
import service.UserService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Path("/user")
public class UserController {

    UserService userService = UserService.getInstance();
    AdminService adminService = AdminService.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProfiles(){
        return userService.getAllProfile();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProfile(Profiles body){
        return userService.createProfile(body);
    }

    @GET
    @Path("seats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBusSeats(@HeaderParam("busId") int busId){
//        MyLogger.run("BusId : "+busId, Level.SEVERE);
        return userService.getBusSeats(busId);
    }

    @POST
    @Path("seats/check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seatCheck(HashMap<String,String> body){
//        MyLogger.run("Check : "+body.toString(),Level.INFO);
        return userService.seatCheck(body);
    }

    @POST
    @Path("seats/book")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seatBook(HashMap<String,String> body,@CookieParam("CToken") String token){
        return userService.seatBook(body,token);
    }

    @GET
    @Path("cancel/details")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCancelDetails(@CookieParam("CToken") String token){
//        MyLogger.run("cancel Token : "+token,Level.INFO);
        String id = adminService.find(token);
        return userService.getCancelDetails(id);
    }

    @POST
    @Path("cancel/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelTickets(HashMap<String,String> body){
//        MyLogger.run(body.get("tickets"), Level.SEVERE);
        return userService.cancelTicket(body);
    }

    @POST
    @Path("fund/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFund(HashMap<String,String> body,@CookieParam("CToken") String token){
        Map<String,String> map = new HashMap<>();
        map.put("wallet",userService.addFund(body.get("amount"),adminService.find(token))+"");
        return Response.status(200).entity(map).build();
    }

    @GET
    @Path("fund")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFund(@CookieParam("CToken") String token){
        Map<String,String> map = new HashMap<>();
        map.put("wallet",userService.getFund(adminService.find(token))+"");
        return Response.status(200).entity(map).build();
    }

}