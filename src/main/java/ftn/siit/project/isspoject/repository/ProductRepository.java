package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByIdAndIsDeletedFalse(int id);
    List<Product> findByIsDeletedFalseOrIsDeletedIsNull();
    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category.name = :category) " +
            "AND (:eventType IS NULL OR EXISTS (SELECT e FROM p.eventTypes e WHERE e.name = :eventType)) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:available IS NULL OR p.isAvailable = :available) " +
            "AND (:visible IS NULL OR p.isVisible = :visible)" +
            "AND (p.isDeleted IS NULL OR p.isDeleted IS FALSE)")
    List<Product> filterProducts(
            @Param("category") String category,
            @Param("eventType") String eventType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("available") Boolean available,
            @Param("visible") Boolean visible);

    List<Product> searchByNameAndIsDeletedFalseOrIsDeletedIsNull(String name);

    List<Product> findByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId);
    Page<Product> findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId, Pageable pageable);
}
