package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Product;

import java.util.List;

public interface ProductService {
    void save(Product product);

    List<Product> findByProvider(Integer providerId);

    List<Product> searchByName(String name);
    List<Product> findAll();
    List<Product> filterProducts(String category, String eventType, Double minPrice, Double maxPrice, Boolean available, Boolean visible);

    Product findById(Integer productId);
}
