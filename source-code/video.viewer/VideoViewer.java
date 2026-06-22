package video.viewer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX WebView-based video viewer with playback controls.
 * Reads configuration from video.viewer/config.xml.
 * Uses native JavaFX MediaPlayer for local audio (WAV/MP3).
 */
public class VideoViewer extends Application
{
    private WebEngine engine;
    private WebView webView;
    private TextField urlBar;
    private MediaPlayer nativePlayer;
    private boolean usingNativePlayer = false;
    private ProgressBar progressBar;
    private Label elapsedLabel;
    private String videoUrl = "";
    private int width = 1280;
    private int height = 720;
    private String title = "Video Viewer";
    private List<String> history = new ArrayList<>();
    private Menu historyMenu;

    @Override
    public void start(Stage stage)
    {
        loadConfig();
        loadHistory();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openLocal = new MenuItem("Open Local Media...");
        openLocal.setOnAction(e -> openLocalFile(stage));
        fileMenu.getItems().add(openLocal);
        menuBar.getMenus().add(fileMenu);

        Menu settingsMenu = new Menu("Settings");
        MenuItem volumeItem = new MenuItem("Volume...");
        volumeItem.setOnAction(e -> showVolumeWindow());
        settingsMenu.getItems().add(volumeItem);
        menuBar.getMenus().add(settingsMenu);

        historyMenu = new Menu("History");
        rebuildHistoryMenu();
        menuBar.getMenus().add(historyMenu);

        // Triple horizontal bar (≡) button on the right side of menu bar
        Button settingsBtn = new Button("\u2261");
        settingsBtn.setOnAction(e -> showVolumeWindow());
        HBox menuBarBox = new HBox(menuBar, settingsBtn);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        settingsBtn.setStyle("-fx-font-weight:bold;");
        menuBarBox.setAlignment(Pos.CENTER_LEFT);

        // URL bar
        urlBar = new TextField(videoUrl);
        urlBar.setPromptText("Enter video URL and press Enter...");
        urlBar.setOnAction(e -> loadVideo(urlBar.getText().trim()));
        HBox.setHgrow(urlBar, Priority.ALWAYS);
        Button goBtn = new Button("Go");
        goBtn.setOnAction(e -> loadVideo(urlBar.getText().trim()));
        Button refreshBtn = new Button("\u21BB");
        refreshBtn.setOnAction(e -> loadVideo(videoUrl));
        HBox urlBox = new HBox(5, urlBar, goBtn, refreshBtn);
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

        startBtn.setOnAction(e -> doPlay());
        stopBtn.setOnAction(e -> doStop());
        pauseBtn.setOnAction(e -> doPause());
        restartBtn.setOnAction(e -> doRestart());

        HBox controls = new HBox(10, startBtn, stopBtn, pauseBtn, restartBtn);
        controls.setPadding(new Insets(5));
        controls.setAlignment(Pos.CENTER);

        // Audio progress bar and elapsed time
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setOnMouseClicked(e -> {
            if (usingNativePlayer && nativePlayer != null && nativePlayer.getTotalDuration() != null)
            {
                double ratio = e.getX() / progressBar.getWidth();
                nativePlayer.seek(nativePlayer.getTotalDuration().multiply(ratio));
            }
        });
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        elapsedLabel = new Label("0:00 / 0:00");
        HBox progressBox = new HBox(10, progressBar, elapsedLabel);
        progressBox.setPadding(new Insets(2, 5, 5, 5));
        progressBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBox.setVisible(false);

        VBox root = new VBox(menuBarBox, urlBox, webView, controls, progressBox);
        stage.setTitle(title);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    private void doPlay()
    {
        if (usingNativePlayer && nativePlayer != null) nativePlayer.play();
        else engine.executeScript("if(ready) player.playVideo()");
    }

    private void doStop()
    {
        if (usingNativePlayer && nativePlayer != null) { nativePlayer.stop(); }
        else engine.executeScript("if(ready) player.stopVideo()");
    }

    private void doPause()
    {
        if (usingNativePlayer && nativePlayer != null) nativePlayer.pause();
        else engine.executeScript("if(ready) player.pauseVideo()");
    }

    private void doRestart()
    {
        if (usingNativePlayer && nativePlayer != null) { nativePlayer.seek(javafx.util.Duration.ZERO); nativePlayer.play(); }
        else engine.executeScript("if(ready){player.seekTo(0,true); player.playVideo();}");
    }

    /**
     * Loads a video by URL. Handles YouTube URLs via IFrame API,
     * local audio via native MediaPlayer, or local video via HTML5.
     */
    private void loadVideo(String url)
    {
        if (url == null || url.isEmpty()) return;
        videoUrl = url;
        urlBar.setText(url);

        // Add to history
        history.remove(url);
        history.add(0, url);
        if (history.size() > 10) history.remove(history.size() - 1);
        saveHistory();
        rebuildHistoryMenu();

        // Stop any existing native player
        if (nativePlayer != null) { nativePlayer.stop(); nativePlayer.dispose(); nativePlayer = null; }
        usingNativePlayer = false;
        progressBar.getParent().setVisible(false);

        String lowerUrl = url.toLowerCase();

        if (url.contains("youtube.com") || url.contains("youtu.be"))
        {
            String videoId = extractVideoId(url);
            String html = "<!DOCTYPE html><html><body style='margin:0'>"
                + "<div id='player'></div>"
                + "<script>var tag=document.createElement('script');tag.src='https://www.youtube.com/iframe_api';document.head.appendChild(tag);"
                + "var player;var ready=false;function onYouTubeIframeAPIReady(){player=new YT.Player('player',{width:'100%',height:'100%',"
                + "videoId:'" + videoId + "',playerVars:{'origin':'https://www.youtube.com','enablejsapi':1},"
                + "events:{'onReady':function(){ready=true;player.playVideo();},"
                + "'onError':function(e){if(e.data===150||e.data===153){document.body.innerHTML="
                + "'<div style=\"display:flex;align-items:center;justify-content:center;height:100%;flex-direction:column\">"
                + "<h2>Video restricted from embedding (Error '+e.data+')</h2>"
                + "<a href=\"https://www.youtube.com/watch?v=" + videoId + "\" style=\"font-size:1.2em\">Watch on YouTube</a></div>';}}"
                + "}});}</script></body></html>";
            engine.loadContent(html);
        }
        else if (lowerUrl.endsWith(".wav") || lowerUrl.endsWith(".mp3"))
        {
            // Use native JavaFX MediaPlayer for audio files
            usingNativePlayer = true;
            nativePlayer = new MediaPlayer(new Media(url));
            nativePlayer.setAutoPlay(true);
            nativePlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                if (nativePlayer == null) return;
                javafx.util.Duration total = nativePlayer.getTotalDuration();
                if (total != null && total.toMillis() > 0 && newVal.toMillis() - oldVal.toMillis() > 100)
                {
                    progressBar.setProgress(newVal.toMillis() / total.toMillis());
                    elapsedLabel.setText(formatTime(newVal) + " / " + formatTime(total));
                }
            });
            progressBar.setProgress(0);
            elapsedLabel.setText("0:00 / 0:00");
            progressBar.getParent().setVisible(true);
            engine.loadContent("<html><body style='margin:0;display:flex;align-items:center;justify-content:center;height:100%'>"
                + "<h2>Playing audio via native player</h2></body></html>");
        }
        else
        {
            // Local video or direct URL via HTML5
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
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.MP4", "*.webm", "*.WEBM", "*.ogg", "*.OGG", "*.avi", "*.AVI", "*.mkv", "*.MKV"),
            new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.WAV", "*.mp3", "*.MP3", "*.ogg", "*.OGG"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fc.showOpenDialog(stage);
        if (file != null) loadVideo(file.toURI().toString());
    }

    private void showVolumeWindow()
    {
        Stage volumeStage = new Stage();
        volumeStage.setTitle("Volume");

        Slider slider = new Slider(0, 100, 100);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setPrefHeight(200);

        Label label = new Label("100");
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int vol = newVal.intValue();
            label.setText(String.valueOf(vol));
            double v = vol / 100.0;
            if (usingNativePlayer && nativePlayer != null) nativePlayer.setVolume(v);
            else engine.executeScript("if(typeof player!=='undefined' && player.setVolume) player.setVolume(" + vol + ");"
                + "var el=document.getElementById('vid'); if(el) el.volume=" + v + ";");
        });

        VBox box = new VBox(10, new Label("Volume"), slider, label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        volumeStage.setScene(new Scene(box, 100, 280));
        volumeStage.show();
    }

    private String formatTime(javafx.util.Duration d)
    {
        int seconds = (int) d.toSeconds();
        return String.format("%d:%02d", seconds / 60, seconds % 60);
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

    private void loadHistory()
    {
        try
        {
            File file = new File("source-code/video.viewer/history.xml");
            if (!file.exists()) file = new File("video.viewer/history.xml");
            if (!file.exists()) return;
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength() && i < 10; i++)
                history.add(items.item(i).getTextContent());
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void saveHistory()
    {
        try
        {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("history");
            doc.appendChild(root);
            for (String url : history)
            {
                Element item = doc.createElement("item");
                item.setTextContent(url);
                root.appendChild(item);
            }
            File outFile = new File("source-code/video.viewer/history.xml");
            if (!outFile.getParentFile().exists()) outFile = new File("video.viewer/history.xml");
            outFile.getParentFile().mkdirs();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(outFile));
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void rebuildHistoryMenu()
    {
        if (historyMenu == null) return;
        historyMenu.getItems().clear();
        for (String url : history)
        {
            MenuItem mi = new MenuItem(url.length() > 60 ? url.substring(0, 60) + "..." : url);
            String u = url;
            mi.setOnAction(e -> loadVideo(u));
            historyMenu.getItems().add(mi);
        }
        if (history.isEmpty()) historyMenu.getItems().add(new MenuItem("(empty)"));
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
