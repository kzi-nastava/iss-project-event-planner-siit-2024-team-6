package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Product;

import java.util.List;

public interface ProductService {
    void save(Product product);

    List<Product> findByProvider(Integer providerId);
}
