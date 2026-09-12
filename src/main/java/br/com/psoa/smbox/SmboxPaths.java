package br.com.psoa.smbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Locations under {@code ${user.home}/smbox} on Linux and Windows.
 */
public final class SmboxPaths {

    private SmboxPaths() {}

    public static Path homeDir() {
        return Path.of(System.getProperty("user.home"), "smbox");
    }

    public static Path dataDir() {
        return homeDir().resolve("data");
    }

    public static Path dbFile() {
        return dataDir().resolve("smbox.db");
    }

    public static Path lockFile() {
        return homeDir().resolve("smbox.lock");
    }

    public static Path windowPrefsFile() {
        return homeDir().resolve("window.properties");
    }

    public static Path logFile() {
        return homeDir().resolve("smbox.log");
    }

    public static void ensureDirectories() {
        try {
            Files.createDirectories(dataDir());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create Smbox data directory: " + dataDir(), e);
        }
    }

    /**
     * SQLite JDBC URL using forward slashes so Windows paths are not treated as escapes.
     */
    public static String jdbcUrl() {
        return "jdbc:sqlite:" + dbFile().toAbsolutePath().toString().replace('\\', '/');
    }
}
