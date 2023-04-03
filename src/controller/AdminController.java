package controller;

import log.MyLogger;
import model.BusDetails;
import model.Profiles;
import service.AdminService;
import service.UserService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.logging.Level;

@Path("admin")
public class AdminController {
    AdminService adminService = AdminService.getInstance();
    UserService userService = UserService.getInstance();

    @GET
    @Path("profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfiles(){
//        MyLogger.run("Get profiles", Level.SEVERE);
        return userService.getAllProfile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBus(){
//        MyLogger.run("Get all bus", Level.SEVERE);
        return adminService.getAllBus();
    }

    @GET
    @Path("id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBusForAdmin(@CookieParam("CToken") String token){
//        MyLogger.run("Get all bus in id", Level.SEVERE);
        return adminService.getAllBusForAdmin(adminService.find(token));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBus(BusDetails body){
//        MyLogger.run(body.getName(), Level.SEVERE);
        return adminService.addBus(body);
    }

    @POST
    @Path("id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBusForAdmin(BusDetails body,@CookieParam("CToken") String token){
//        MyLogger.run(body.getName(), Level.SEVERE);
        return adminService.addBusForAdmin(body, adminService.find(token));
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBusForAdmin(BusDetails body,@CookieParam("CToken") String token){
//        MyLogger.run("update", Level.SEVERE);
        String user =  adminService.find(token);
        return adminService.updateBusForAdmin(body,user);
    }

    @DELETE
    @Path("id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBusForAdmin(BusDetails body){
//        MyLogger.run("delete", Level.SEVERE);
        return adminService.deleteBusForAdmin(body.getId());
    }

    @POST
    @Path("new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProfile(Profiles body){
        body.setRole(1);
        return userService.createProfile(body);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdmin(Profiles profiles){
//        MyLogger.run("delete", Level.SEVERE);
        return adminService.deleteAdmin(profiles.getEmail_id());
    }

    @GET
    @Path("bookings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookings(@CookieParam("CToken") String token){
        MyLogger.run("Hi : "+adminService.find(token), Level.INFO);
        return adminService.getBookings(adminService.find(token));
    }

    @POST
    @Path("password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(HashMap<String,String> body,@CookieParam("CToken")String token){
        return adminService.changePassword(body,adminService.find(token));
    }
}
