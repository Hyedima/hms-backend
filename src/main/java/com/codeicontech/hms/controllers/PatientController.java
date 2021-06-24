package com.codeicontech.hms.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import com.codeicontech.hms.data.models.Patient;
import com.codeicontech.hms.data.repositories.PatientRepository;
import com.codeicontech.hms.payload.request.patient.PatientRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    @Autowired
    PatientRepository patientRepository;

    @GetMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<Patient>> getPatients() {
        try {

            List<Patient> patients = new ArrayList<>();

            patientRepository.findAll().forEach(patients::add);

            return new ResponseEntity<>(patients, HttpStatus.OK);

        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search-by-code/{code}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Patient> searchPatientByCode(@PathVariable("code") String code) {
        Optional<Patient> patientData = patientRepository.findByCode(code);

        if(patientData.isPresent()) {
            return new ResponseEntity<>(patientData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search-by-name")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<Patient>> searchPatientByName(@RequestParam(required = true) String name) {
        try {

            List<Patient> patients = new ArrayList<>();

            patientRepository.findByFullName(name).forEach(patients::add);

            return new ResponseEntity<>(patients, HttpStatus.OK);

        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") long id) {
        Optional<Patient> patientData = patientRepository.findById(id);

        if(patientData.isPresent()) {
            return new ResponseEntity<>(patientData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        try {

            Patient patient = patientRepository.save(
                new Patient(
                    generateUniqueCode(),
                    patientRequest.getFullName(),
                    patientRequest.getEmail(),
                    patientRequest.getPhone(),
                    patientRequest.getAddress(),
                    patientRequest.getDob()
                )
            );

            return new ResponseEntity<>(patient, HttpStatus.CREATED);

        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") long id, @Valid @RequestBody PatientRequest patientRequest) {
        Optional<Patient> patientData = patientRepository.findById(id);

        try {
            if(patientData.isPresent()) {
                Patient patient = patientData.get();
                patient.setFullName(patientRequest.getFullName());
                patient.setEmail(patientRequest.getEmail());
                patient.setPhone(patientRequest.getPhone());
                patient.setAddress(patientRequest.getAddress());
                patient.setDob(patientRequest.getDob());

                Patient savedPatient = patientRepository.save(patient);

                return new ResponseEntity<>(savedPatient, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	public ResponseEntity<HttpStatus> deletePatient(@PathVariable("id") long id) {
		try {
			patientRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    private String generateUniqueCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

        return generatedString;
    }
}