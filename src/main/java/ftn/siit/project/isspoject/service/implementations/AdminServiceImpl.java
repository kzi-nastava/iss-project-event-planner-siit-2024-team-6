package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Admin;
import ftn.siit.project.isspoject.repository.AdminRepository;
import ftn.siit.project.isspoject.service.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByEmailAndIsActiveIsTrue(email);
    }
}
