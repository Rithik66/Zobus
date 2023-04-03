package controller;

import model.Profiles;
import service.UserService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("login")
public class LoginController {

    UserService userService = UserService.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("role")String role, Profiles profile){
        int intRole = Integer.parseInt(role);
        return Response.ok().build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Profiles profile){
        Response response = userService.validate(profile);
        if(response.getStatus()==200){
            return userService.createProfile(profile);
        }
        return response;
    }
}
