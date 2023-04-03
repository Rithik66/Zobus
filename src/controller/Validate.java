package controller;

import service.AdminService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("validate")
public class Validate {

    AdminService adminService = AdminService.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("CToken") String token){
//        MyLogger.run("CookieParam "+token, Level.INFO);
        return adminService.validate(token);
    }

}
