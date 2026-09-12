
package br.com.psoa.smbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class SmboxApplication extends Application {

	private static ConfigurableApplicationContext springContext;
	private static final CompletableFuture<Integer> portFuture = new CompletableFuture<>();

	public static void main(String[] args) {
		Application.launch(SmboxApplication.class, args);
	}

	@Override
	public void init() {
		springContext = new SpringApplicationBuilder(SmboxApplication.class)
			.properties(
				"server.address=127.0.0.1",
				"server.port=0",
				"spring.datasource.url=jdbc:sqlite:" + resolveDbPath()
			)
			.run(getParameters().getRaw().toArray(new String[0]));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onReady(ApplicationReadyEvent event) {
		int port = ((WebServerApplicationContext) event.getApplicationContext())
			.getWebServer().getPort();
		portFuture.complete(port);
	}

	@Override
	public void start(Stage stage) throws Exception {
		int port = portFuture.get();
		WebView webView = new WebView();
		webView.getEngine().load("http://127.0.0.1:" + port + "/");
		stage.setScene(new Scene(webView, 1200, 800));
		stage.setTitle("Smbox");
		stage.setOnCloseRequest(e -> Platform.exit());
		stage.show();
	}

	@Override
	public void stop() {
		if (springContext != null) {
			springContext.close();
		}
	}

	private static String resolveDbPath() {
		return System.getProperty("user.home") + File.separator
			+ "smbox" + File.separator + "data" + File.separator + "smbox.db";
	}

}
