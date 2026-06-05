package com.autobook.ControllerTest;

import com.autobook.Storage.StorageController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageControllerTest {

    private final StorageController storageController = new StorageController();

    @Test
    void uploadImage_emptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);
        ResponseEntity<String> response = storageController.uploadImage(file);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
