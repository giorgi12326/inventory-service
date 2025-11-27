package org.example.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.example.dto.ProductInfoDTO;
import org.example.service.ProductService;

@Path("/api/product")
public class ProductController {
    @Inject
    ProductService productService;

    @POST
    public Response addProduct(ProductInfoDTO productInfoDTO) {
        return Response.status(Response.Status.CREATED).entity(productService.addProduct(productInfoDTO)).build();
    }
}



