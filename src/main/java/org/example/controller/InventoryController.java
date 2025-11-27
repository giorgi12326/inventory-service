package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.InventoryRequestDTO;
import org.example.service.InventoryService;


@Path("/api/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryController {

    @Inject
    InventoryService inventoryService;

    @GET
    public Response getInventories() {
        return Response.ok(inventoryService.getInventories()).build();
    }
    @POST
    public Response addInventory(InventoryRequestDTO inventoryRequestDTO) {
        System.out.println(inventoryRequestDTO); // test if it's parsed
        return Response.status(Response.Status.CREATED).entity(inventoryService.addInventory(inventoryRequestDTO)).build();
    }

}
