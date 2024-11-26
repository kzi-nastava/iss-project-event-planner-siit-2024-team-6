package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Budget;

public interface BudgetService {
    Budget findById(Integer id);
    Budget save(Budget budget);
    Budget update(Budget budget);
    void delete(Budget budget);
}
