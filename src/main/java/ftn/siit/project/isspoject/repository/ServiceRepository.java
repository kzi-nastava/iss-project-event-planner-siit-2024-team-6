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

    @Query(value = "SELECT DISTINCT o.* FROM offers o " +
            "JOIN categories c ON c.id = o.category_id " +
            "LEFT JOIN offer_event_types et ON et.offer_id = o.id " +
            "LEFT JOIN event_types e ON e.id = et.event_type_id " +
            "WHERE (:providerId IS NULL OR o.provider_id = :providerId) " +
            "AND (:categories IS NULL OR c.name = ANY (CAST(:categories AS TEXT[]))) " +
            "AND (:eventTypes IS NULL OR e.name = ANY (CAST(:eventTypes AS TEXT[]))) " +
            "AND (:isAvailable IS NULL OR o.is_available = :isAvailable) " +
            "AND (:price IS NULL OR :price IS 0.0 OR o.price <= :price) " +
            "AND (o.is_deleted IS NULL OR o.is_deleted = FALSE) " +
            "AND o.offer_type = 'Service'",
            countQuery = "SELECT COUNT(DISTINCT o.id) FROM offers o " +
                    "JOIN categories c ON c.id = o.category_id " +
                    "LEFT JOIN offer_event_types et ON et.offer_id = o.id " +
                    "LEFT JOIN event_types e ON e.id = et.event_type_id " +
                    "WHERE (:providerId IS NULL OR o.provider_id = :providerId) " +
                    "AND (:categories IS NULL OR c.name = ANY (CAST(:categories AS TEXT[]))) " +
                    "AND (:eventTypes IS NULL OR e.name = ANY (CAST(:eventTypes AS TEXT[]))) " +
                    "AND (:isAvailable IS NULL OR o.is_available = :isAvailable) " +
                    "AND (:price IS NULL OR :price IS 0.0 OR o.price <= :price) " +
                    "AND (o.is_deleted IS NULL OR o.is_deleted = FALSE) " +
                    "AND o.offer_type = 'Service'",
            nativeQuery = true)
    Page<Service> findServicesByFilters(
            @Param("providerId") Integer providerId,
            @Param("categories") String[] categories,
            @Param("eventTypes") String[] eventTypes,
            @Param("isAvailable") Boolean isAvailable,
            @Param("price") Double price,
            Pageable pageable);


    @Query(value = "SELECT o.* FROM offers o " +
            "WHERE (:providerId IS NULL OR o.provider_id = :providerId) " +
            "AND (:name IS NULL OR LOWER(CAST(o.name AS TEXT)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (o.is_deleted IS NULL OR o.is_deleted = FALSE) " +
            "AND o.offer_type = 'Service'",
            countQuery = "SELECT COUNT(*) FROM offers o " +
                    "WHERE (:providerId IS NULL OR o.provider_id = :providerId) " +
                    "AND (:name IS NULL OR LOWER(CAST(o.name AS TEXT)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (o.is_deleted IS NULL OR o.is_deleted = FALSE) " +
                    "AND o.offer_type = 'Service'",
            nativeQuery = true)
    Page<Service> searchByName(@Param("providerId") Integer providerId,
                               @Param("name") String name, Pageable page);


    List<Service> findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId);

    Page<Service> findAllByProviderIdAndIsDeletedFalseOrIsDeletedIsNull(int providerId, Pageable pageable);

}
