/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.MobileNotificationModel;
import com.hr.backend.fin_model.NeedsRequestModel;
import com.hr.backend.fin_repository.MobileNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileNotificationService {

    @Autowired
    private MobileNotificationRepository notificationRepository;

    public void notifyRole(
            String organization,
            String role,
            String title,
            String message,
            String type,
            NeedsRequestModel request) {

        MobileNotificationModel notification = new MobileNotificationModel();

        notification.setOrganization(organization);
        notification.setUserRole(role);
        notification.setUserEmail(null);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);

        if (request != null) {
            notification.setRelatedRequestId(request.getId());
            notification.setRelatedRequestNo(request.getRequestNo());
        }

        notification.setReadStatus(false);

        notificationRepository.save(notification);
    }

    public void notifyUser(
            String organization,
            String email,
            String title,
            String message,
            String type,
            NeedsRequestModel request) {

        MobileNotificationModel notification = new MobileNotificationModel();

        notification.setOrganization(organization);
        notification.setUserEmail(email);
        notification.setUserRole(null);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);

        if (request != null) {
            notification.setRelatedRequestId(request.getId());
            notification.setRelatedRequestNo(request.getRequestNo());
        }

        notification.setReadStatus(false);

        notificationRepository.save(notification);
    }
}
