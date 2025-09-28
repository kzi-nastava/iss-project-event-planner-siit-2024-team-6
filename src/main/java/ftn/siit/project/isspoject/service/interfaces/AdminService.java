package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Admin;
import ftn.siit.project.isspoject.entity.Provider;

public interface AdminService {
    Admin findByEmail(String email);
}
