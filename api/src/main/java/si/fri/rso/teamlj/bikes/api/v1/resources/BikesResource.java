package si.fri.rso.teamlj.bikes.api.v1.resources;

import si.fri.rso.teamlj.bikes.entities.Bike;
import si.fri.rso.teamlj.bikes.services.beans.BikesBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/bikes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BikesResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private BikesBean bikesBean;

    @GET
    public Response getBikes() {

        List<Bike> bikes = bikesBean.getBikes(uriInfo);

        return Response.ok(bikes).build();
    }

    @GET
    @Path("/{bikeId}")
    public Response getBike(@PathParam("bikeId") Integer bikeId) {

        Bike bike = bikesBean.getBike(bikeId);

        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(bike).build();
    }

    @POST
    public Response createBike(Bike bike) {

        bike = bikesBean.createBike(bike);

        if (bike.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(bike).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(bike).build();
        }
    }

    @PUT
    @Path("/{bikeId}")
    public Response putBike(@PathParam("bikeId") Integer bikeId, Bike bike) {

        bike = bikesBean.putBike(bikeId, bike);

        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (bike.getId() != null)
                return Response.status(Response.Status.OK).entity(bike).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @PATCH
    @Path("/{bikeId}/taken")
    public Response bikeTaken(@PathParam("bikeId") Integer bikeId) {

        Bike bike = bikesBean.bikeTaken(bikeId);

        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (bike.getId() != null)
                return Response.status(Response.Status.OK).entity(bike).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @PUT
    @Path("/{bikeId}/free/{lan}&{lon}")
    public Response bikeFree(@PathParam("bikeId") Integer bikeId, @PathParam("lan") Float latitude, @PathParam("lon") Float longitude, Bike bike) {

        bike = bikesBean.bikeFree(bikeId, latitude, longitude, bike);

        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (bike.getId() != null)
                return Response.status(Response.Status.OK).entity(bike).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }


    @DELETE
    @Path("/{bikeId}")
    public Response deleteBike(@PathParam("bikeId") Integer bikeId) {

        boolean deleted = bikesBean.deleteBike(bikeId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
