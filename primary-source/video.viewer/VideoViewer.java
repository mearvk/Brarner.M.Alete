package video.viewer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

/**
 * JavaFX WebView-based video viewer with playback controls.
 * Reads configuration from video.viewer/config.xml.
 */
public class VideoViewer extends Application
{
    private WebEngine engine;
    private String videoUrl = "about:blank";
    private int width = 1280;
    private int height = 720;
    private String title = "Video Viewer";

    @Override
    public void start(Stage stage)
    {
        loadConfig();

        WebView webView = new WebView();
        engine = webView.getEngine();
        engine.load(videoUrl);

        Button startBtn = new Button("Start");
        Button stopBtn = new Button("Stop");
        Button pauseBtn = new Button("Pause");
        Button restartBtn = new Button("Restart");

        startBtn.setOnAction(e -> engine.executeScript("document.querySelector('video').play()"));
        stopBtn.setOnAction(e -> engine.executeScript("document.querySelector('video').pause(); document.querySelector('video').currentTime = 0"));
        pauseBtn.setOnAction(e -> engine.executeScript("document.querySelector('video').pause()"));
        restartBtn.setOnAction(e -> engine.executeScript("document.querySelector('video').currentTime = 0; document.querySelector('video').play()"));

        HBox controls = new HBox(10, startBtn, stopBtn, pauseBtn, restartBtn);
        controls.setPadding(new Insets(5));

        VBox root = new VBox(webView, controls);
        stage.setTitle(title);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    private void loadConfig()
    {
        try
        {
            File configFile = new File("video.viewer/config.xml");
            if (!configFile.exists()) return;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);
            Element root = doc.getDocumentElement();

            if (root.hasAttribute("url")) videoUrl = root.getAttribute("url");
            if (root.hasAttribute("width")) width = Integer.parseInt(root.getAttribute("width"));
            if (root.hasAttribute("height")) height = Integer.parseInt(root.getAttribute("height"));
            if (root.hasAttribute("title")) title = root.getAttribute("title");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
