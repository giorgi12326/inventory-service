package org.example.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ProductInfoDTO;
import org.example.service.ProductService;

@Path("/api/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    @Inject
    ProductService productService;

    @POST
    public Response addProduct(ProductInfoDTO productInfoDTO) {
        return Response.status(Response.Status.CREATED).entity(productService.addProduct(productInfoDTO)).build();
    }
}



