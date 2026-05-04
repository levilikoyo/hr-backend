/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class StorageService {
/*
    @Value("${GCS_BUCKET_NAME}")
    private String bucketName;

    public String uploadFile(byte[] fileBytes, String fileName, String contentType) {

        Storage storage = StorageOptions.getDefaultInstance().getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, fileBytes);

        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }
*/
}
