package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    Optional<Service> findByIdAndIsDeletedFalse(int id);

    List<Service> findByIsDeletedFalse();

    @Query("SELECT o FROM Service o " +
            "WHERE (:provider_id IS NULL OR o.provider.id = :provider_id) " +
            "AND (:name IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:category IS NULL OR o.category.name = :category) " +
            "AND (:eventType IS NULL OR EXISTS (SELECT e FROM o.eventTypes e WHERE e.name = :eventType)) " +
            "AND (:price IS NULL OR o.price <= :price) " +
            "AND (:isAvailable IS NULL OR o.isAvailable = :isAvailable) " +
            "AND (o.isDeleted IS NULL OR o.isDeleted IS FALSE)")
    List<Service> findFilteredServices(
            @Param("provider_id") int providerId,
            @Param("name") String name,
            @Param("category") String category,
            @Param("eventType") String eventType,
            @Param("price") Double price,
            @Param("isAvailable") Boolean isAvailable);

    List<Service> findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId);
    Page<Service> findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId, Pageable pageable);

}
