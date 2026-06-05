package com.autobook.Backup;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Service dedicated to performing database backups.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BookRepository bookRepository;

    /**
     * Executes an asynchronous backup of the primary domain logic entities.
     * <p>
     * Writing a large dataset can block the HTTP Thread, so this executes
     * dynamically in a background worker.
     * </p>
     */
    @Async
    public void createDatabaseBackup() {
        log.info("Spawning background worker thread for Database Backup...");
        long startTime = System.currentTimeMillis();

        try {
            List<Book> allBooks = bookRepository.findAll();
            log.info("Found {} books to backup.", allBooks.size());

            if (!allBooks.isEmpty()) {
                Book sampleBook = allBooks.get(0);
                Map<String, Object> reflectionData = ReflectionUtil.inspectObjectFields(sampleBook);
                log.info("Audited first entity dynamically: {}", reflectionData);
            }

            String backupFilePath = "autobook_backup.dat";
            try (FileOutputStream fileOut = new FileOutputStream(backupFilePath);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                out.writeObject(allBooks);
                log.info("Successfully wrote entity states to {}", backupFilePath);

            } catch (Exception e) {
                log.error("Failed to perform output stream backup", e);
            }

        } catch (Exception e) {
            log.error("Fatal exception during asynchronous backup", e);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Background backup task finished successfully in {} ms.", duration);
    }
}
