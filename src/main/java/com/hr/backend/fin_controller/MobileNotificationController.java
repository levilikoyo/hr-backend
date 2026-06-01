/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.MobileNotificationModel;
import com.hr.backend.fin_repository.MobileNotificationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile-notifications")
@CrossOrigin(origins = "*")
public class MobileNotificationController {

    @Autowired
    private MobileNotificationRepository notificationRepository;

    @GetMapping("/test")
    public String test() {
        return "Mobile Notifications API is working";
    }

    @GetMapping("/my-notifications")
    public List<MobileNotificationModel> getMyNotifications(
            @RequestParam String organization,
            @RequestParam String email,
            @RequestParam String role) {

        List<MobileNotificationModel> result = new ArrayList<>();

        result.addAll(notificationRepository.findByOrganizationAndUserEmailOrderByIdDesc(
                organization,
                email
        ));

        result.addAll(notificationRepository.findByOrganizationAndUserRoleOrderByIdDesc(
                organization,
                role
        ));

        result.sort(Comparator.comparing(MobileNotificationModel::getId).reversed());

        return result;
    }

    @GetMapping("/unread-count")
    public int getUnreadCount(
            @RequestParam String organization,
            @RequestParam String email,
            @RequestParam String role) {

        int emailCount = notificationRepository
                .findByOrganizationAndUserEmailAndReadStatusFalseOrderByIdDesc(
                        organization,
                        email
                ).size();

        int roleCount = notificationRepository
                .findByOrganizationAndUserRoleAndReadStatusFalseOrderByIdDesc(
                        organization,
                        role
                ).size();

        return emailCount + roleCount;
    }

    @PutMapping("/{id}/mark-read")
    public MobileNotificationModel markRead(@PathVariable Long id) {

        MobileNotificationModel notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setReadStatus(true);
        notification.setReadAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @PutMapping("/mark-all-read")
    public String markAllRead(
            @RequestParam String organization,
            @RequestParam String email,
            @RequestParam String role) {

        List<MobileNotificationModel> notifications = new ArrayList<>();

        notifications.addAll(notificationRepository
                .findByOrganizationAndUserEmailAndReadStatusFalseOrderByIdDesc(
                        organization,
                        email
                ));

        notifications.addAll(notificationRepository
                .findByOrganizationAndUserRoleAndReadStatusFalseOrderByIdDesc(
                        organization,
                        role
                ));

        for (MobileNotificationModel notification : notifications) {
            notification.setReadStatus(true);
            notification.setReadAt(LocalDateTime.now());
        }

        notificationRepository.saveAll(notifications);

        return "All notifications marked as read";
    }
}
