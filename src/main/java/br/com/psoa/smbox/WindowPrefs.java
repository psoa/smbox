package br.com.psoa.smbox;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

final class WindowPrefs {

    private static final double DEFAULT_WIDTH = 1200;
    private static final double DEFAULT_HEIGHT = 800;

    private WindowPrefs() {}

    static void restore(Stage stage) {
        Properties props = load();
        double width = parse(props, "width", DEFAULT_WIDTH);
        double height = parse(props, "height", DEFAULT_HEIGHT);
        Rectangle2D visual = Screen.getPrimary().getVisualBounds();
        width = clamp(width, 640, visual.getWidth());
        height = clamp(height, 480, visual.getHeight());
        stage.setWidth(width);
        stage.setHeight(height);

        if (props.containsKey("x") && props.containsKey("y")) {
            double x = parse(props, "x", visual.getMinX() + (visual.getWidth() - width) / 2);
            double y = parse(props, "y", visual.getMinY() + (visual.getHeight() - height) / 2);
            if (intersects(x, y, width, height, visual)) {
                stage.setX(x);
                stage.setY(y);
            }
        }
        stage.setMaximized("true".equalsIgnoreCase(props.getProperty("maximized")));
    }

    static void save(Stage stage) {
        Properties props = new Properties();
        boolean maximized = stage.isMaximized();
        props.setProperty("maximized", Boolean.toString(maximized));
        // Use restore bounds so un-maximizing later does not collapse to the maximized size.
        props.setProperty("x", Double.toString(stage.getX()));
        props.setProperty("y", Double.toString(stage.getY()));
        props.setProperty("width", Double.toString(stage.getWidth()));
        props.setProperty("height", Double.toString(stage.getHeight()));
        SmboxPaths.ensureDirectories();
        try (OutputStream out = Files.newOutputStream(SmboxPaths.windowPrefsFile())) {
            props.store(out, "Smbox window position");
        } catch (IOException ignored) {
        }
    }

    private static Properties load() {
        Properties props = new Properties();
        if (!Files.isRegularFile(SmboxPaths.windowPrefsFile())) {
            return props;
        }
        try (InputStream in = Files.newInputStream(SmboxPaths.windowPrefsFile())) {
            props.load(in);
        } catch (IOException ignored) {
        }
        return props;
    }

    private static double parse(Properties props, String key, double fallback) {
        try {
            return Double.parseDouble(props.getProperty(key, Double.toString(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean intersects(double x, double y, double width, double height, Rectangle2D visual) {
        return x + width > visual.getMinX()
            && y + height > visual.getMinY()
            && x < visual.getMaxX()
            && y < visual.getMaxY();
    }
}
