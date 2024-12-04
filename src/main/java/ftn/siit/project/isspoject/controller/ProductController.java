package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.offer.NewProductDTO;
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

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody NewProductDTO productDTO) {
        Category category = categoryService.findByName(productDTO.getCategory().getName());

        if (category == null) {
            categoryService.createPendingCategory(productDTO.getCategory().getName());
            notificationService.notifyAdmin("New category suggestion: " + productDTO.getCategory());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // Категория ожидает одобрения
        }

        // Создаём продукт
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSale(productDTO.getSale());
        product.setPhotos(productDTO.getPhotos());
        product.setIsVisible(productDTO.getIsVisible());
        product.setIsAvailable(productDTO.getIsAvailable());
        product.setCategory(category);

        Product savedProduct = productService.save(product);

        // Преобразуем сохранённый продукт в DTO
        ProductDTO responseDTO = toProductDTO(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); // Возвращаем созданный продукт
    }
    private ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setSale(product.getSale());
        productDTO.setPhotos(product.getPhotos());
        productDTO.setIsVisible(product.getIsVisible());
        productDTO.setIsAvailable(product.getIsAvailable());
        productDTO.setCategory(product.getCategory());
        return productDTO;
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

    @PutMapping("{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer productId, @RequestBody NewProductDTO productDTO) {
        Product product = productService.findById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Продукт не найден
        }

        if (productDTO.getType() != null && productDTO.getType().equalsIgnoreCase("Service")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Нельзя изменить тип продукта
        }

        // Обновление полей продукта
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSale(productDTO.getSale());
        product.setPhotos(productDTO.getPhotos());
        product.setIsVisible(productDTO.getIsVisible());
        product.setIsAvailable(productDTO.getIsAvailable());
        product.setLastChanged(LocalDateTime.now());

        Product updatedProduct = productService.save(product);

        // Преобразование в DTO
        ProductDTO updatedProductDTO = toProductDTO(updatedProduct);

        return ResponseEntity.ok(updatedProductDTO); // Возвращаем обновлённый продукт
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Integer productId) {
        Product product = productService.findById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Продукт не найден
        }

        // Логическое удаление
        product.setIsDeleted(true);
        Product deletedProduct = productService.save(product);

        // Преобразование в DTO
        ProductDTO deletedProductDTO = toProductDTO(deletedProduct);

        return ResponseEntity.ok(deletedProductDTO); // Возвращаем "удалённый" продукт
    }

}

