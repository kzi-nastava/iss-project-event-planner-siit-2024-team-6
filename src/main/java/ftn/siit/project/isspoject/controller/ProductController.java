package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.OfferDTO;
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

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/{providerId}/list")
    public ResponseEntity<List<ProductDTO>> getProductsByProvider(@PathVariable Integer providerId) {
        List<Product> products = productService.findByProvider(providerId);
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();

            OfferDTO offerDTO = new OfferDTO(product);

            // Копируем данные из offerDTO в productDTO
            productDTO.setId(offerDTO.getId());
            productDTO.setStatus(offerDTO.getStatus());
            productDTO.setName(offerDTO.getName());
            productDTO.setDescription(offerDTO.getDescription());
            productDTO.setPrice(offerDTO.getPrice());
            productDTO.setSale(offerDTO.getSale());
            productDTO.setPhotos(offerDTO.getPhotos());
            productDTO.setIsVisible(offerDTO.getIsVisible());
            productDTO.setIsAvailable(offerDTO.getIsAvailable());
            productDTO.setIsDeleted(offerDTO.getIsDeleted());
            productDTO.setLastChanged(offerDTO.getLastChanged());
            productDTO.setCategory(offerDTO.getCategory());
            productDTO.setType(offerDTO.getType());

            return productDTO;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

}

