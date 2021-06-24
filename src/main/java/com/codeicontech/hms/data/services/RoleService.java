package com.codeicontech.hms.data.services;

import com.codeicontech.hms.data.constants.AppRole;
import com.codeicontech.hms.data.models.Role;
import com.codeicontech.hms.data.repositories.RoleRepository;
import com.codeicontech.hms.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RoleService {

//    private

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
//        userRepository.deleteAll();

        var roles = this.roleRepository.findAll();
        if(roles.isEmpty()){
            Role adminRole = new Role(AppRole.ROLE_ADMIN);
            this.roleRepository.save(adminRole);

            Role userRole = new Role(AppRole.ROLE_USER);
            this.roleRepository.save(userRole);
        }
    }

}
