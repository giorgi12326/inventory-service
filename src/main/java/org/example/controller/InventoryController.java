package org.example.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.example.service.InventoryService;


@Path("/api")
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
    public Response addProduct(ProductInfo productInfo) {
        return Response.status(Response.Status.CREATED).entity(inventoryService.addProduct(productInfo)).build();
    }
}
