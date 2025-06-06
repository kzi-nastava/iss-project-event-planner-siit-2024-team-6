package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Purchase;
import ftn.siit.project.isspoject.repository.PurchaseRepository;
import ftn.siit.project.isspoject.service.interfaces.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public Purchase findPurchaseById(int id) {
        return null;
    }

    @Override
    public Boolean existsPurchase(int organizerId, int productId) {
        for (Purchase purchase : purchaseRepository.findAll()) {
            if(purchase.getOrganizer().getId() == organizerId && purchase.getProduct().getId() == productId) {
                return true;
            }
        }
        return false;
    }
}
