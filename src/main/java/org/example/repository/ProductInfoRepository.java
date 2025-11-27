package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.ProductInfo;

@ApplicationScoped
public class ProductInfoRepository implements PanacheRepository<ProductInfo> {
}
