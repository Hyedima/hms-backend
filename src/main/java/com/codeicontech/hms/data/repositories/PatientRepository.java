package com.codeicontech.hms.data.repositories;

import java.util.List;
import java.util.Optional;

import com.codeicontech.hms.data.models.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByCode(String code);

    List<Patient> findByFullName(String name);
}
