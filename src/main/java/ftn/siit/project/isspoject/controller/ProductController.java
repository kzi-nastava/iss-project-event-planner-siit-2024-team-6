package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.dto.offer.ProductDTO;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import ftn.siit.project.isspoject.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping()
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

//        product.setIsVisible(productDTO.getIsVisible());
//        product.setIsAvailable(productDTO.getIsAvailable());
        product.setCategory(category);

        productService.save(product);
        return new ResponseEntity<>("Product created successfully", HttpStatus.CREATED);
    }
//    @GetMapping
//    public ResponseEntity<PagedResponse<ProductDTO>> getProductsPage(Pageable pageable) {
//        Page<Product> productPage = productService.findAll(pageable);
//
//        List<ProductDTO> productDTOs = productPage.stream()
//                .map(ProductDTO::new)
//                .toList();
//
//        PagedResponse<ProductDTO> response = new PagedResponse<>(
//                productDTOs,
//                productPage.getTotalPages(),
//                productPage.getTotalElements()
//        );
//
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("{providerId}/list")
    public ResponseEntity<List<ProductDTO>> getProductsByProvider(@PathVariable Integer providerId) {
        List<Product> products = productService.findByProvider(providerId);
        return getListResponseEntity(products);
    }

    @GetMapping("search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchByName(name);
        return getListResponseEntity(products);
    }

    @GetMapping("filter")
    public ResponseEntity<List<ProductDTO>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Boolean visible) {
        List<Product> products = productService.filterProducts(category, eventType, minPrice, maxPrice, available, visible);
        return getListResponseEntity(products);
    }

    private ResponseEntity<List<ProductDTO>> getListResponseEntity(List<Product> products) {
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();

            OfferDTO offerDTO = new OfferDTO(product);

            productDTO.setId(offerDTO.getId());
            productDTO.setStatus(offerDTO.getStatus());
            productDTO.setName(offerDTO.getName());
            productDTO.setDescription(offerDTO.getDescription());
            productDTO.setPrice(offerDTO.getPrice());
            productDTO.setSale(offerDTO.getSale());
            productDTO.setPhotos(offerDTO.getPhotos());
//            productDTO.setIsVisible(offerDTO.getIsVisible());
//            productDTO.setIsAvailable(offerDTO.getIsAvailable());
//            productDTO.setIsDeleted(offerDTO.getIsDeleted());
//            productDTO.setLastChanged(offerDTO.getLastChanged());
            productDTO.setCategory(offerDTO.getCategory());
            productDTO.setType(offerDTO.getType());

            return productDTO;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

    @PutMapping("{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer productId, @RequestBody ProductDTO productDTO) {
        Product product = productService.findById(productId);
        if (product == null) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSale(productDTO.getSale());
        product.setPhotos(productDTO.getPhotos());
//        product.setIsVisible(productDTO.getIsVisible());
//        product.setIsAvailable(productDTO.getIsAvailable());
        product.setLastChanged(LocalDateTime.now());


        if (productDTO.getType() != null && productDTO.getType().equalsIgnoreCase("Service")) {
            return new ResponseEntity<>("Product type cannot be changed to Service", HttpStatus.BAD_REQUEST);
        }

        productService.save(product);
        return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
    }
    @DeleteMapping("{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        product.setIsDeleted(true);
        productService.save(product);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }
}

