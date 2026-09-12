package br.com.psoa.smbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmboxPathsTest {

    private String previousHome;

    @AfterEach
    void restoreHome() {
        if (previousHome != null) {
            System.setProperty("user.home", previousHome);
        }
    }

    @Test
    void ensureDirectoriesCreatesDataDirAndJdbcUrlUsesForwardSlashes(@TempDir Path tempHome) {
        previousHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());

        assertFalse(Files.isDirectory(SmboxPaths.dataDir()));
        SmboxPaths.ensureDirectories();
        assertTrue(Files.isDirectory(SmboxPaths.dataDir()));

        String url = SmboxPaths.jdbcUrl();
        assertTrue(url.startsWith("jdbc:sqlite:"));
        assertFalse(url.contains("\\"), url);
        assertTrue(url.replace('\\', '/').endsWith("/smbox/data/smbox.db"));
        assertEquals(tempHome.resolve("smbox").resolve("data").resolve("smbox.db"), SmboxPaths.dbFile());
    }
}
