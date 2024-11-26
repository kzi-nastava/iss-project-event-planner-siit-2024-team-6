package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Override
    public void save(Product product) {

    }

    @Override
    public List<Product> findByProvider(Integer providerId) {
        return null;
    }

    @Override
    public List<Product> searchByName(String name) {
        return null;
    }

    @Override
    public List<Product> filterProducts(String category, String eventType, Double minPrice, Double maxPrice, Boolean available, Boolean visible) {
        return null;
    }

    @Override
    public Product findById(Integer productId) {
        return null;
    }
}
