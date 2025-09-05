package com.visit.studentTracker.controller;

import com.visit.studentTracker.service.DataMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
@Slf4j
public class DataMigrationController {

    private final DataMigrationService dataMigrationService;

    @PostMapping("/start")
    public ResponseEntity<String> startMigration() {
        try {
            log.info("Starting data migration process...");
            dataMigrationService.migrateData();
            return ResponseEntity.ok("Data migration completed successfully!");
        } catch (Exception e) {
            log.error("Data migration failed: ", e);
            return ResponseEntity.internalServerError()
                    .body("Data migration failed: " + e.getMessage());
        }
    }

}
