package com.restaurant.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile file;

    @Test
    void uploadImageReturnsSecureUrlFromCloudinaryResponse() throws Exception {
        CloudinaryService service = new CloudinaryService("cloud", "key", "secret");
        ReflectionTestUtils.setField(service, "cloudinary", cloudinary);

        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://img.test/menu.jpg"));

        String result = service.uploadImage(file);

        assertEquals("https://img.test/menu.jpg", result);
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadImagePropagatesIOExceptionFromMultipartFile() throws Exception {
        CloudinaryService service = new CloudinaryService("cloud", "key", "secret");
        ReflectionTestUtils.setField(service, "cloudinary", cloudinary);

        when(file.getBytes()).thenThrow(new IOException("read failed"));

        IOException ex = assertThrows(IOException.class, () -> service.uploadImage(file));

        assertEquals("read failed", ex.getMessage());
    }
}

