package org.example.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ProductInfoDTO;
import org.example.dto.UpdateQuantityFromInventory;
import org.example.service.ProductService;

@Path("/api/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    @Inject
    ProductService productService;

    @GET
    public Response getProducts() {
        return Response.status(Response.Status.OK).entity(productService.getProducts()).build();
    }

    @POST
    public Response addProduct(ProductInfoDTO productInfoDTO) {
        return Response.status(Response.Status.CREATED).entity(productService.addProduct(productInfoDTO)).build();
    }

    @POST
    @Path("/quantity")
    public Response updateProductQuantity(UpdateQuantityFromInventory updateQuantityFromInventory) {
        return Response.ok(productService.updateProduct(updateQuantityFromInventory)).build();
    }
}



