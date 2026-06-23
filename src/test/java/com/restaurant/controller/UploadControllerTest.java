package com.restaurant.controller;

import com.restaurant.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UploadController controller;

    @Test
    void uploadReturnsUrlWhenUploadSucceeds() throws Exception {
        when(cloudinaryService.uploadImage(multipartFile)).thenReturn("https://cdn.example.com/pic.jpg");

        ResponseEntity<Map<String, String>> response = controller.upload(multipartFile);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("https://cdn.example.com/pic.jpg", response.getBody().get("url"));
    }

    @Test
    void uploadReturnsServerErrorWhenUploadFails() throws Exception {
        when(cloudinaryService.uploadImage(multipartFile)).thenThrow(new RuntimeException("cloud down"));

        ResponseEntity<Map<String, String>> response = controller.upload(multipartFile);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().get("error").contains("cloud down"));
    }
}

