package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{
    @Override
    public void save(Product product) {
        System.out.println(product);
    }

    @Override
    public List<Product> findByProvider(Integer providerId) {
        // Get all products
        List<Product> products = findAll();

        // Filter products by provider ID
        return products.stream()
                .filter(product -> product.getProvider() != null && product.getProvider().getId().equals(providerId))
                .collect(Collectors.toList());
    }
//    @Override
//    public Page<Product> findAll(Pageable pageable) {
//        return productRepository.findAll(pageable); // Используем метод JPA для пагинации
//    }

    @Override
    public List<Product> searchByName(String name) {
        // Get all products
        List<Product> products = findAll();

        // Filter products by name (case-insensitive)
        return products.stream()
                .filter(product -> product.getName() != null && product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


    @Override
    public List<Product> findAll() {
        Provider provider = new Provider();
        provider.setId(100);
        provider.setName("Ivan");

        Provider provider2 = new Provider();
        provider2.setId(101);
        provider2.setName("Jovan");

        Product product1 = new Product();
        product1.setId(1);
        product1.setName("London Event Chair");
        product1.setDescription("Comfortable chair suitable for conferences and events.");
        product1.setPrice(50.0);
        product1.setSale(10.0);
        product1.setPhotos(List.of("https://example.com/london-chair.jpg"));
        product1.setIsVisible(true);
        product1.setIsAvailable(true);
        product1.setIsDeleted(false);
        product1.setLastChanged(LocalDateTime.now().minusDays(2));
        product1.setProvider(provider);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Manchester Stage Lighting");
        product2.setDescription("Professional stage lighting for concerts and events.");
        product2.setPrice(500.0);
        product2.setSale(15.0);
        product2.setPhotos(List.of("https://example.com/manchester-lighting.jpg"));
        product2.setIsVisible(true);
        product2.setIsAvailable(false);
        product2.setIsDeleted(false);
        product2.setLastChanged(LocalDateTime.now().minusDays(5));
        product2.setProvider(provider);

        Product product3 = new Product();
        product3.setId(3);
        product3.setName("Edinburgh Catering Service");
        product3.setDescription("High-quality catering service for weddings and parties.");
        product3.setPrice(200.0);
        product3.setSale(5.0);
        product3.setPhotos(List.of("https://example.com/edinburgh-catering.jpg"));
        product3.setIsVisible(false);
        product3.setIsAvailable(true);
        product3.setIsDeleted(false);
        product3.setLastChanged(LocalDateTime.now().minusDays(10));
        product3.setProvider(provider2);

        // Return the fake list of products
        return List.of(product1, product2, product3);
    }

    @Override
    public List<Product> filterProducts(String category, String eventType, Double minPrice, Double maxPrice, Boolean available, Boolean visible) {
        // Get all products
        List<Product> products = findAll();

        // Apply filters using Stream API
        return products.stream()
                .filter(product ->
                        (category == null || (product.getCategory() != null && product.getCategory().equals(category))) &&
                                (eventType == null || (product.getEventTypes() != null && product.getEventTypes().contains(eventType))) &&
                                (minPrice == null || product.getPrice() >= minPrice) &&
                                (maxPrice == null || product.getPrice() <= maxPrice) &&
                                (available == null || product.getIsAvailable().equals(available)) &&
                                (visible == null || product.getIsVisible().equals(visible))
                )
                .collect(Collectors.toList());
    }


    @Override
    public Product findById(Integer productId) {
        // Get all products
        List<Product> products = findAll();

        // Find the product by its ID
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null); // Return null if no match is found
    }

}
