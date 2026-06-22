package io.github.gaeqs.javayoutubedownloader.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.gaeqs.javayoutubedownloader.stream.EncodedStream;
import io.github.gaeqs.javayoutubedownloader.stream.YoutubeVideo;
import io.github.gaeqs.javayoutubedownloader.util.EncodedStreamUtils;
import io.github.gaeqs.javayoutubedownloader.util.HTMLUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a decoder that uses the HTML5 web of youtube to decode stream options. This decoder is the most
 * safe to use, giving solid results.
 * <p>
 * This protocol won't work if the video has an age restriction, or if it's not accessible in the country
 * of the running machine.
 * <p>
 * Its default name in the {@link io.github.gaeqs.javayoutubedownloader.decoder.DecoderManager} is "html".
 */
public class HTMLDecoder implements io.github.gaeqs.javayoutubedownloader.decoder.Decoder
{

    private static final String YOUTUBE_URL = "https://youtube.com";

    private static final Pattern YT_PLAYER_RESPONSE = Pattern.compile("var ytInitialPlayerResponse = (\\{.*?});");
    private static final Pattern YT_PLAYER_JS_URL = Pattern.compile("\"jsUrl\":\\s*\"(.*?)\"");

    private static final String KEY_STREAMING_DATA = "streamingData";
    private static final String KEY_VIDEO_DETAILS = "videoDetails";

    private static final String KEY_FORMATS = "formats";
    private static final String KEY_ADAPTIVE_FORMATS = "adaptiveFormats";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";

    private String urlEncoding;

    public HTMLDecoder() {
        this("UTF-8");
    }

    public HTMLDecoder(String urlEncoding) {
        this.urlEncoding = urlEncoding;
    }

    public String getUrlEncoding() {
        return urlEncoding;
    }

    public void setUrlEncoding(String urlEncoding) {
        this.urlEncoding = urlEncoding;
    }

    @Override
    public YoutubeVideo extractVideo(URL url) throws IOException {
        String html = HTMLUtils.readAll(url);
        String rawResponse = matchAndGet(YT_PLAYER_RESPONSE, html);

        JSONObject response = JSON.parseObject(rawResponse);
        JSONObject streamingData = response.getJSONObject(KEY_STREAMING_DATA);
        JSONObject details = response.getJSONObject(KEY_VIDEO_DETAILS);

        String jsUrl = YOUTUBE_URL + matchAndGet(YT_PLAYER_JS_URL, html);

        Set<EncodedStream> encodedStreams = new HashSet<>();

        if (streamingData.containsKey(KEY_FORMATS)) {
            streamingData.getJSONArray(KEY_FORMATS).forEach(o -> parseFormat(o, encodedStreams));
        }
        if (streamingData.containsKey(KEY_ADAPTIVE_FORMATS)) {
            streamingData.getJSONArray(KEY_ADAPTIVE_FORMATS).forEach(o -> parseFormat(o, encodedStreams));
        }

        YoutubeVideo video = new YoutubeVideo(details.getString(KEY_TITLE), details.getString(KEY_AUTHOR), null);

        encodedStreams.removeIf(target -> !target.decode(jsUrl, false));
        encodedStreams.forEach(target -> video.getStreamOptions().add(target.getDecodedStream()));

        return video;
    }

    private void parseFormat(Object object, Collection<EncodedStream> collection) {
        if (object instanceof JSONObject) {
            try {
                EncodedStreamUtils.addEncodedStreams((JSONObject) object, collection, urlEncoding);
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error while parsing URL.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Extracts video details (title, author) from a YouTube URL without decoding streams.
     *
     * @param url the YouTube video URL.
     * @return a YoutubeVideo with title and author but no stream options.
     * @throws IOException if an IO error occurs.
     */
    public YoutubeVideo extractVideoDetails(URL url) throws IOException {
        String html = HTMLUtils.readAll(url);
        String rawResponse = matchAndGet(YT_PLAYER_RESPONSE, html);
        JSONObject response = JSON.parseObject(rawResponse);
        JSONObject details = response.getJSONObject(KEY_VIDEO_DETAILS);
        String title = details != null ? details.getString(KEY_TITLE) : "Unknown";
        String author = details != null ? details.getString(KEY_AUTHOR) : null;
        return new YoutubeVideo(title, author);
    }

    /**
     * Extracts the video ID from a YouTube URL string.
     *
     * @param url the YouTube URL.
     * @return the video ID.
     */
    public static String extractVideoId(String url) {
        if (url.contains("v=")) {
            String id = url.substring(url.indexOf("v=") + 2);
            int amp = id.indexOf('&');
            return amp > 0 ? id.substring(0, amp) : id;
        }
        if (url.contains("youtu.be/")) {
            String id = url.substring(url.indexOf("youtu.be/") + 9);
            int q = id.indexOf('?');
            return q > 0 ? id.substring(0, q) : id;
        }
        return url.trim();
    }

    private String matchAndGet(Pattern pattern, String data) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            throw new NoSuchElementException("Match not found!");
        }
        return matcher.group(1);
    }
}