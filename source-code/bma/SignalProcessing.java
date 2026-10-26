package bma;

import com.github.psambit9791.jdsp.transform.FastFourier;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Shared signal-processing engine for every Brarner.M.Alete instance.
 *
 * <p>Historically this class was copy-pasted ~9,000 times (once per instance),
 * with each copy differing only in package name, config path, port and output
 * labels. Those differences are all data, so they now live in each instance's
 * {@code config.xml} and this single class is parameterised by an {@link Instance}
 * identity instead.</p>
 *
 * <p>Construct with the instance's qualified name (e.g. {@code "universities.appalachian"})
 * and the path to its {@code config.xml}, then call {@link Audio#process()},
 * {@link Data#process()} or {@link Graphics#process()}.</p>
 */
public final class SignalProcessing {

    /** Immutable per-instance configuration loaded from a {@code config.xml}. */
    public static final class Config {
        public final String dataSource;
        public final String dataOutput;
        public final String socketHost;
        public final int socketPort;
        public final String localInputPath;
        public final String dbUrl;
        public final String dbUser;
        public final String dbPass;
        public final String outputDir;

        Config(String dataSource, String dataOutput, String socketHost, int socketPort,
               String localInputPath, String dbUrl, String dbUser, String dbPass, String outputDir) {
            this.dataSource = dataSource;
            this.dataOutput = dataOutput;
            this.socketHost = socketHost;
            this.socketPort = socketPort;
            this.localInputPath = localInputPath;
            this.dbUrl = dbUrl;
            this.dbUser = dbUser;
            this.dbPass = dbPass;
            this.outputDir = outputDir;
        }
    }

    private final String qualifiedName; // e.g. "universities.appalachian"
    private final String shortName;     // e.g. "appalachian"
    private final Config config;

    /**
     * @param qualifiedName dotted instance name, e.g. {@code "universities.appalachian"}
     * @param configPath    path to this instance's {@code config.xml}
     * @throws IOException if the config file cannot be read or parsed. Unlike the
     *                     original copy-pasted code, this fails loudly instead of
     *                     silently falling back to a hardcoded port 9000.
     */
    public SignalProcessing(String qualifiedName, String configPath) throws IOException {
        this.qualifiedName = qualifiedName;
        int dot = qualifiedName.lastIndexOf('.');
        this.shortName = dot >= 0 ? qualifiedName.substring(dot + 1) : qualifiedName;
        this.config = loadConfig(configPath);
    }

    public Config config() {
        return config;
    }

    private static Config loadConfig(String path) throws IOException {
        File file = new File(path);
        if (!file.isFile()) {
            throw new FileNotFoundException("Instance config not found: " + file.getPath()
                + " (working dir: " + System.getProperty("user.dir") + ")");
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();
            String portText = getTag(doc, "socket-port");
            int port = portText.isEmpty() ? -1 : Integer.parseInt(portText.trim());
            return new Config(
                getTag(doc, "data-source"),
                getTag(doc, "data-output"),
                getTag(doc, "socket-host"),
                port,
                getTag(doc, "local-input-path"),
                getTag(doc, "db-url"),
                getTag(doc, "db-user"),
                getTag(doc, "db-pass"),
                getTag(doc, "output-dir"));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to parse instance config: " + path, e);
        }
    }

    private static String getTag(Document doc, String tag) {
        NodeList nl = doc.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
    }

    private double[] readData() throws IOException {
        if ("socket".equals(config.dataSource)) {
            try (Socket sock = new Socket(config.socketHost, config.socketPort);
                 DataInputStream dis = new DataInputStream(sock.getInputStream())) {
                int len = dis.readInt();
                double[] data = new double[len];
                for (int i = 0; i < len; i++) data[i] = dis.readDouble();
                return data;
            }
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(config.localInputPath))) {
            return br.lines().mapToDouble(Double::parseDouble).toArray();
        }
    }

    private void writeToFile(String filename, String content) throws IOException {
        File f = new File(config.outputDir, filename + ".rdns");
        File parent = f.getParentFile();
        if (parent != null) parent.mkdirs();
        try (Writer w = Files.newBufferedWriter(f.toPath())) {
            w.write(content);
        }
    }

    private void writeToDatabase(String expName, String data) throws SQLException {
        try (Connection conn = DriverManager.getConnection(config.dbUrl, config.dbUser, config.dbPass);
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO experiments (experiment_name, experiment_data) VALUES (?, ?)")) {
            ps.setString(1, expName);
            ps.setString(2, data);
            ps.executeUpdate();
        }
    }

    private void output(String filename, String expName, String content) throws IOException, SQLException {
        if ("file".equals(config.dataOutput) || "both".equals(config.dataOutput)) {
            writeToFile(filename, content);
        }
        if ("database".equals(config.dataOutput) || "both".equals(config.dataOutput)) {
            writeToDatabase(expName, content);
        }
    }

    /** Audio FFT: magnitude spectrum of the input signal. */
    public final class Audio {
        public void process() throws IOException, SQLException {
            double[] raw = readData();
            FastFourier fft = new FastFourier(raw);
            fft.transform();
            double[] mag = fft.getMagnitude(true);
            StringBuilder sb = new StringBuilder();
            for (double v : mag) sb.append(v).append("\n");
            output(shortName + "_audio", qualifiedName + ".audio.fft", sb.toString());
        }
    }

    /** Statistical analysis: mean and variance of the input signal. */
    public final class Data {
        public void process() throws IOException, SQLException {
            double[] raw = readData();
            double sum = 0;
            for (double v : raw) sum += v;
            double mean = sum / raw.length;
            double var = 0;
            for (double v : raw) var += (v - mean) * (v - mean);
            var /= raw.length;
            output(shortName + "_data", qualifiedName + ".data.stats",
                "mean=" + mean + "\nvariance=" + var + "\nn=" + raw.length + "\n");
        }
    }

    /** Graphics spectrum: FFT magnitude as a plottable x,y series. */
    public final class Graphics {
        public void process() throws IOException, SQLException {
            double[] raw = readData();
            FastFourier fft = new FastFourier(raw);
            fft.transform();
            double[] mag = fft.getMagnitude(true);
            StringBuilder sb = new StringBuilder("x,y\n");
            for (int i = 0; i < mag.length; i++) sb.append(i).append(",").append(mag[i]).append("\n");
            output(shortName + "_graphics", qualifiedName + ".graphics.spectrum", sb.toString());
        }
    }

    public Audio audio() { return new Audio(); }
    public Data data() { return new Data(); }
    public Graphics graphics() { return new Graphics(); }
}
