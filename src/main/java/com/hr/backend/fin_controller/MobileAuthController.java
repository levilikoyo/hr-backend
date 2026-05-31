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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        if (request.getOrganization() == null || request.getOrganization().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Organization is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (request.getPinCode() == null || request.getPinCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("PIN code is required");
        }

        Optional<MobileUserModel> optionalUser =
                mobileUserRepository.findByOrganizationAndEmailAndActiveTrue(
                        request.getOrganization(),
                        request.getEmail().trim()
                );

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or inactive user");
        }

        MobileUserModel user = optionalUser.get();

        if (!user.getPinCode().equals(request.getPinCode().trim())) {
            return ResponseEntity.status(401).body("Invalid PIN code");
        }

        MobileLoginResponse response = new MobileLoginResponse(
                user.getId(),
                user.getOrganization(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getUserRole()
        );

        return ResponseEntity.ok(response);
    }
}
