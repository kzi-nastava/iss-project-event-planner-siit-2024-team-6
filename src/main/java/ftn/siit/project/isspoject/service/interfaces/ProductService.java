package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Product save(Product product);
    Product update(Product product);
    void delete(Product product);
    List<Product> searchByName(String name);
    List<Product> findAll();
    List<Product> filterProducts(String category, String eventType, Double minPrice, Double maxPrice, Boolean available, Boolean visible);
    Product findById(Integer productId);
    List<Product> findByProvider(int providerId);

    Page<Product> findByProvider(Provider p, Pageable page);

    Offer update(int offerId, NewOfferDTO dto, List<EventType> eventTypes);
}
