/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */


import com.hr.backend.fin_model.MobileLoginRequest;
import com.hr.backend.fin_model.MobileLoginResponse;
import com.hr.backend.fin_model.MobileUserModel;
import com.hr.backend.fin_repository.MobileUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/mobile-auth")
@CrossOrigin(origins = "*")
public class MobileAuthController {

    @Autowired
    private MobileUserRepository mobileUserRepository;

    @GetMapping("/test")
    public String test() {
        return "Mobile Auth API is working";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MobileLoginRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid login request");
        }

        String email = safe(request.getEmail()).toLowerCase();
        String pinCode = safe(request.getPinCode());

        if (email.isEmpty() || pinCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and PIN are required");
        }

        Optional<MobileUserModel> optionalUser =
                mobileUserRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or PIN");
        }

        MobileUserModel user = optionalUser.get();

        if (Boolean.FALSE.equals(user.getActive())) {
            return ResponseEntity.status(403).body("This mobile user is inactive");
        }

        if (!pinCode.equals(safe(user.getPinCode()))) {
            return ResponseEntity.status(401).body("Invalid email or PIN");
        }

        MobileLoginResponse response = new MobileLoginResponse();

        response.setId(user.getId());
        response.setOrganization(user.getOrganization());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getUserRole());
        response.setActive(user.getActive());
        response.setMessage("Login successful");

        return ResponseEntity.ok(response);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}