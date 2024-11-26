package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.ProductDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.service.CategoryService;
import ftn.siit.project.isspoject.service.NotificationService;
import ftn.siit.project.isspoject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO productDTO) {

        Category category = categoryService.findByName(productDTO.getCategory());
        if (category == null) {

            categoryService.createPendingCategory(productDTO.getCategory());
            notificationService.notifyAdmin("New category suggestion: " + productDTO.getCategory());
            return new ResponseEntity<>("Category suggestion submitted for approval", HttpStatus.ACCEPTED);
        }

        // Создаем продукт
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSale(productDTO.getSale());
        product.setPhotos(productDTO.getPhotos());

        product.setIsVisible(productDTO.getIsVisible());
        product.setIsAvailable(productDTO.getIsAvailable());
        product.setCategory(category);

        productService.save(product);
        return new ResponseEntity<>("Product created successfully", HttpStatus.CREATED);
    }

}

