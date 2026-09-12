package br.com.psoa.smbox;

import javafx.application.Application;

/**
 * JVM entry point. Must not extend {@link Application}; otherwise {@code java}
 * requires JavaFX on the module path and {@code ./gradlew bootRun} fails with
 * "JavaFX runtime components are missing".
 */
public final class SmboxLauncher {

    public static void main(String[] args) {
        SmboxPaths.ensureDirectories();
        if (!SingleInstance.tryAcquire()) {
            System.err.println("Smbox is already running.");
            Application.launch(AlreadyRunningApp.class, args);
            return;
        }
        Application.launch(SmboxApplication.class, args);
    }

    private SmboxLauncher() {}
}
