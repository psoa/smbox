package br.com.psoa.smbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SmboxApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static final CompletableFuture<Integer> portFuture = new CompletableFuture<>();

    @Override
    public void init() {
        SmboxPaths.ensureDirectories();
        try {
            springContext = new SpringApplicationBuilder(SmboxApplication.class)
                .headless(false)
                .properties(
                    "server.address=127.0.0.1",
                    "server.port=0",
                    "spring.datasource.url=" + SmboxPaths.jdbcUrl()
                )
                .run(getParameters().getRaw().toArray(new String[0]));
        } catch (Exception e) {
            portFuture.completeExceptionally(e);
            throw e;
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        int port = ((WebServerApplicationContext) event.getApplicationContext())
            .getWebServer().getPort();
        portFuture.complete(port);
    }

    @Override
    public void start(Stage stage) throws Exception {
        int port = portFuture.get(60, TimeUnit.SECONDS);
        WebView webView = new WebView();
        webView.getEngine().load("http://127.0.0.1:" + port + "/");
        stage.setScene(new Scene(webView));
        stage.setTitle("Smbox");
        loadIcon(stage);
        WindowPrefs.restore(stage);
        stage.setOnCloseRequest(e -> {
            WindowPrefs.save(stage);
            Platform.exit();
        });
        stage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    private static void loadIcon(Stage stage) {
        try (InputStream in = SmboxApplication.class.getResourceAsStream("/icons/smbox.png")) {
            if (in != null) {
                stage.getIcons().add(new Image(in));
            }
        } catch (Exception ignored) {
        }
    }
}
