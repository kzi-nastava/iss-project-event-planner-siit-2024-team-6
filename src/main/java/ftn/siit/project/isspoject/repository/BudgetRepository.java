package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer>{
    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.budgetItems WHERE b.id = :id")
    Optional<Budget> findByIdWithItems(@Param("id") Integer id);
}
