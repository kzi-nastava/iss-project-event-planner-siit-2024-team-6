package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public Category findByName(String category) {
        return null;
    }

    @Override
    public void createPendingCategory(String category) {

    }
}
