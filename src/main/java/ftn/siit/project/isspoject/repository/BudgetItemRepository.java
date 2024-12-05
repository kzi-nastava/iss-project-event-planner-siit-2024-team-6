package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Integer>{
}
