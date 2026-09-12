package br.com.psoa.smbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleInstanceTest {

    private String previousHome;

    @AfterEach
    void restoreHome() {
        if (previousHome != null) {
            System.setProperty("user.home", previousHome);
        }
    }

    @Test
    void secondLockFromAnotherChannelFails(@TempDir Path tempHome) throws Exception {
        previousHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
        SmboxPaths.ensureDirectories();

        try (FileChannel first = FileChannel.open(
                SmboxPaths.lockFile(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
             var lock = first.tryLock()) {
            assertTrue(lock != null && lock.isValid());
            assertFalse(SingleInstance.tryAcquire());
        }
    }
}
