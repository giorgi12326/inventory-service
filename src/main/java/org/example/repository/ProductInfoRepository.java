package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.ProductInfo;

import java.util.Optional;

@ApplicationScoped
public class ProductInfoRepository implements PanacheRepository<ProductInfo> {
    public Optional<ProductInfo> findByProductId(Long productId) {
        return find("productId", productId).firstResultOptional();
    }

}
