package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Purchase;

public interface PurchaseService {
    Purchase save(Purchase purchase);
    Purchase findPurchaseById(int id);
    Boolean existsPurchase(int organizerId, int productId);
}
