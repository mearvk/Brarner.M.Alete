package video.viewer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
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
    private WebView webView;
    private TextField urlBar;
    private String videoUrl = "about:blank";
    private int width = 1280;
    private int height = 720;
    private String title = "Video Viewer";

    @Override
    public void start(Stage stage)
    {
        loadConfig();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openLocal = new MenuItem("Open Local Media...");
        openLocal.setOnAction(e -> openLocalFile(stage));
        fileMenu.getItems().add(openLocal);
        menuBar.getMenus().add(fileMenu);

        // URL bar
        urlBar = new TextField(videoUrl);
        urlBar.setPromptText("Enter video URL and press Enter...");
        urlBar.setOnAction(e -> loadVideo(urlBar.getText().trim()));
        HBox.setHgrow(urlBar, Priority.ALWAYS);
        Button goBtn = new Button("Go");
        goBtn.setOnAction(e -> loadVideo(urlBar.getText().trim()));
        HBox urlBox = new HBox(5, urlBar, goBtn);
        urlBox.setPadding(new Insets(5));

        // WebView
        webView = new WebView();
        engine = webView.getEngine();
        VBox.setVgrow(webView, Priority.ALWAYS);

        // Load initial video
        loadVideo(videoUrl);

        // Playback controls
        Button startBtn = new Button("Start");
        Button stopBtn = new Button("Stop");
        Button pauseBtn = new Button("Pause");
        Button restartBtn = new Button("Restart");

        startBtn.setOnAction(e -> engine.executeScript("if(ready) player.playVideo()"));
        stopBtn.setOnAction(e -> engine.executeScript("if(ready) player.stopVideo()"));
        pauseBtn.setOnAction(e -> engine.executeScript("if(ready) player.pauseVideo()"));
        restartBtn.setOnAction(e -> engine.executeScript("if(ready){player.seekTo(0,true); player.playVideo();}"));

        HBox controls = new HBox(10, startBtn, stopBtn, pauseBtn, restartBtn);
        controls.setPadding(new Insets(5));
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(menuBar, urlBox, webView, controls);
        stage.setTitle(title);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    /**
     * Loads a video by URL. Handles YouTube URLs via IFrame API,
     * or local/direct URLs via HTML5 video element.
     */
    private void loadVideo(String url)
    {
        if (url == null || url.isEmpty()) return;
        videoUrl = url;
        urlBar.setText(url);

        if (url.contains("youtube.com") || url.contains("youtu.be"))
        {
            String videoId = extractVideoId(url);
            String html = "<!DOCTYPE html><html><body style='margin:0'>"
                + "<div id='player'></div>"
                + "<script>var tag=document.createElement('script');tag.src='https://www.youtube.com/iframe_api';document.head.appendChild(tag);"
                + "var player;var ready=false;function onYouTubeIframeAPIReady(){player=new YT.Player('player',{width:'100%',height:'100%',"
                + "videoId:'" + videoId + "',events:{'onReady':function(){ready=true;player.playVideo();}}});}</script></body></html>";
            engine.loadContent(html);
        }
        else
        {
            // Local file or direct video URL
            String html = "<!DOCTYPE html><html><body style='margin:0'>"
                + "<video id='vid' width='100%' height='100%' controls autoplay>"
                + "<source src='" + url + "'></video>"
                + "<script>var ready=true;var player={playVideo:function(){document.getElementById('vid').play();},"
                + "stopVideo:function(){var v=document.getElementById('vid');v.pause();v.currentTime=0;},"
                + "pauseVideo:function(){document.getElementById('vid').pause();},"
                + "seekTo:function(t){document.getElementById('vid').currentTime=t;}};</script></body></html>";
            engine.loadContent(html);
        }
    }

    private void openLocalFile(Stage stage)
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Media File");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.webm", "*.ogg", "*.avi", "*.mkv"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fc.showOpenDialog(stage);
        if (file != null) loadVideo(file.toURI().toString());
    }

    private String extractVideoId(String url)
    {
        if (url.contains("v="))
        {
            String id = url.substring(url.indexOf("v=") + 2);
            int amp = id.indexOf('&');
            return amp > 0 ? id.substring(0, amp) : id;
        }
        if (url.contains("youtu.be/"))
        {
            return url.substring(url.lastIndexOf('/') + 1);
        }
        return url;
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
