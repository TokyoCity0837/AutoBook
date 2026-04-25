package com.autobook.Storage;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageControllerTest {

    @Test
    void testUploadEmptyFile() {
        StorageController controller = new StorageController();
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        ResponseEntity<String> response = controller.uploadImage(file);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please select a file to upload.", response.getBody());
    }

    @Test
    void testUploadValidFile() {
        StorageController controller = new StorageController();
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test content".getBytes());
        ResponseEntity<String> response = controller.uploadImage(file);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("/api/images/"));
        assertTrue(response.getBody().endsWith(".png"));
    }

    @Test
    void testUploadIOException() {
        StorageController controller = new StorageController();
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test content".getBytes()) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("Testing IO Exception");
            }
        };
        ResponseEntity<String> response = controller.uploadImage(file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to upload the file.", response.getBody());
    }
}
