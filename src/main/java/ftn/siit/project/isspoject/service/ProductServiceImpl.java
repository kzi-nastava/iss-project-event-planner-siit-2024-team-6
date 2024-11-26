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
}
