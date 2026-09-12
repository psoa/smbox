package br.com.psoa.smbox;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.StandardOpenOption;

/**
 * Process-wide exclusive lock on {@code ~/smbox/smbox.lock}. The lock is released
 * when the JVM exits, including after a crash, so a stale file is not a problem.
 */
public final class SingleInstance implements AutoCloseable {

    private static SingleInstance held;

    private final FileChannel channel;
    private final FileLock lock;

    private SingleInstance(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /**
     * @return {@code true} if this process now owns the instance lock
     */
    public static synchronized boolean tryAcquire() {
        if (held != null) {
            return true;
        }
        SmboxPaths.ensureDirectories();
        FileChannel channel = null;
        try {
            channel = FileChannel.open(
                SmboxPaths.lockFile(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            );
            FileLock lock = channel.tryLock();
            if (lock == null) {
                return false;
            }
            held = new SingleInstance(channel, lock);
            Runtime.getRuntime().addShutdownHook(new Thread(held::close));
            channel = null;
            return true;
        } catch (OverlappingFileLockException e) {
            return false;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot acquire Smbox instance lock", e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (SingleInstance.class) {
            try {
                if (lock != null && lock.isValid()) {
                    lock.release();
                }
            } catch (IOException ignored) {
            }
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException ignored) {
            }
            if (held == this) {
                held = null;
            }
        }
    }
}
