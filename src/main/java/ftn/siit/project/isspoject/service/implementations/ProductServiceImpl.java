package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ProductRepository;
import ftn.siit.project.isspoject.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product) {
        Product existingProduct = findById(product.getId());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setIsDeleted(false);
        existingProduct.setSale(product.getSale());
        existingProduct.setEventTypes(product.getEventTypes());
        existingProduct.setIsAvailable(product.getIsAvailable());
        existingProduct.setIsVisible(product.getIsVisible());
        existingProduct.setLastChanged(LocalDateTime.now());
        return productRepository.save(existingProduct);
    }

    @Override
    public void delete(Product product) {
        product.setIsDeleted(true);
        productRepository.save(product);
    }

//    @Override
//    public Page<Product> findAll(Pageable pageable) {
//        return productRepository.findAll(pageable); // Используем метод JPA для пагинации
//    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.searchByNameAndIsDeletedFalseOrIsDeletedIsNull(name);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findByIsDeletedFalseOrIsDeletedIsNull();
    }

    @Override
    public List<Product> filterProducts(String category, String eventType, Double minPrice, Double maxPrice, Boolean available, Boolean visible) {
        return productRepository.filterProducts(category, eventType, minPrice, maxPrice, available, visible);
    }

    @Override
    public Product findById(Integer productId) {
        Product p = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        if (p.getIsDeleted()) {
            throw new NotFoundException("Product is deleted");
        }
        return p;
    }

    @Override
    public List<Product> findByProvider(int providerId) {
        return productRepository.findByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(providerId);
    }
    @Override
    public Page<Product> findByProvider(Provider p, Pageable page) {
        return productRepository.findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(p.getId(), page);
    }

}
