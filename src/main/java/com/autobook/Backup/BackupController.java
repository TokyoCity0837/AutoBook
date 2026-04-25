package com.autobook.Backup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerBackup() {
        // triggers the asynchronous backup task
        backupService.createDatabaseBackup();
        return ResponseEntity.ok("Backup process has been started asynchronously in the background.");
    }
}
