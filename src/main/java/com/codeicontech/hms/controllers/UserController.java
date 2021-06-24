package com.codeicontech.hms.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.codeicontech.hms.data.constants.AppRole;
import com.codeicontech.hms.data.models.Role;
import com.codeicontech.hms.data.models.User;
import com.codeicontech.hms.data.repositories.RoleRepository;
import com.codeicontech.hms.data.repositories.UserRepository;
import com.codeicontech.hms.payload.request.user.UserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
	RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        try {

            List<User> users = new ArrayList<>();

//            userRepository.findAll().forEach((user) -> {
//                user.getRoles().forEach(role -> {
//                    if(role.getRoleName() == AppRole.ROLE_USER) {
//                        users.add(user);
//                    }
//                });
//            });

            userRepository.findAll().forEach(users::add);

            return new ResponseEntity<>(users, HttpStatus.OK);

        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        Optional<User> userData = userRepository.findById(id);

        if(userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> addUser(@Valid @RequestBody UserRequest userRequest) {
        try {

            User user = userRepository.save(
                new User(
                    userRequest.getFullName(),
                    userRequest.getEmail(),
                    userRequest.getEmail(),
                    userRequest.getPassword()
                )
            );

            return new ResponseEntity<>(user, HttpStatus.CREATED);

        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @Valid @RequestBody UserRequest userRequest) {
        Optional<User> userData = userRepository.findById(id);

        try {
            if(userData.isPresent()) {
                Set<Role> roles = new HashSet<>();
                Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
                
                User user = userData.get();
                user.setFullName(userRequest.getFullName());
                user.setUsername(userRequest.getEmail());
                user.setEmail(userRequest.getEmail());
                user.setPassword(userRequest.getPassword());
                user.setRoles(roles);

                User savedUser = userRepository.save(user);

                return new ResponseEntity<>(savedUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
		try {
			userRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
