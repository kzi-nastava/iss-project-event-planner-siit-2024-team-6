package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
    List<Offer> findTop5ByIsDeletedFalseAndIsAvailableTrueAndStatusOrderByPriceAsc(Status status);
    @Query("SELECT o FROM Offer o WHERE o.isDeleted = false AND o.isVisible = true AND o.status = 1")
    Page<Offer> findAllAccepted(Pageable pageable);

    List<Offer> findByIsDeletedFalseOrIsDeletedIsNull();

    Optional<Offer> findByIdAndIsDeletedFalse(int id);

    List<Offer> findByProviderAndIsDeletedFalseOrIsDeletedIsNull(Provider provider);

    @Query("SELECT o FROM Offer o " +
            "WHERE (:name IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:description IS NULL OR LOWER(o.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
            "AND (:minPrice IS NULL OR o.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.price <= :maxPrice) " +
            "AND (:startDate IS NULL OR o.lastChanged >= :startDate) " +
            "AND (:endDate IS NULL OR o.lastChanged <= :endDate) " +
            "AND (:category IS NULL OR o.category.name = :category) " +
            "AND (:isService IS NULL OR " +
            "     (:isService = TRUE AND TYPE(o) = ftn.siit.project.isspoject.entity.Service) OR " +
            "     (:isService = FALSE AND TYPE(o) <> ftn.siit.project.isspoject.entity.Service))")
    List<Offer> searchItems(
            @Param("name") String name,
            @Param("description") String description,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("category") String category,
            @Param("isService") Boolean isService);

    @Query("SELECT o FROM Offer o WHERE o.category.id = :category_id AND (o.isDeleted = false OR o.isDeleted IS NULL)")
    List<Offer> findNonDeletedOffersByCategory(@Param("category_id") int categoryId);
}
