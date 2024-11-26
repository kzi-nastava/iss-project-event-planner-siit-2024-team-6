package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Category;

public interface CategoryService {
    Category findByName(String category);

    void createPendingCategory(String category);
}
