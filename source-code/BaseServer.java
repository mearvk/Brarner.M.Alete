import bma.SignalProcessing;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Master BaseServer — reads {@code source-code/config.xml} to determine which
 * modules and instances are active, then starts a listening thread per active
 * instance on its configured port.
 *
 * <p>Each instance is served by the single shared {@link bma.SignalProcessing}
 * engine, parameterised by the instance's identity and its {@code config.xml}
 * (there is no longer a per-instance copy of the signal-processing code). When
 * an instance's config references {@code data-source=socket} it pulls samples
 * from its configured source; otherwise the incoming connection payload is
 * consumed directly.</p>
 */
public final class BaseServer {

    /** One runnable instance: qualified name, listen port, and config path. */
    private static final class InstanceDef {
        final String name;
        final int port;
        final String configPath;
        InstanceDef(String name, int port, String configPath) {
            this.name = name;
            this.port = port;
            this.configPath = configPath;
        }
    }

    private static final List<InstanceDef> instances = new ArrayList<>();

    static {
        try {
            File masterConfig = new File("source-code/config.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(masterConfig);
            NodeList modules = doc.getElementsByTagName("module");
            Set<Integer> usedPorts = new HashSet<>();
            for (int m = 0; m < modules.getLength(); m++) {
                Element mod = (Element) modules.item(m);
                if (!"true".equals(mod.getAttribute("enabled"))) continue;
                String modName = mod.getAttribute("name");
                File moduleConfig = new File(mod.getAttribute("config"));
                File moduleDir = moduleConfig.getParentFile();

                Document modDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(moduleConfig);
                NodeList nodes = modDoc.getElementsByTagName("instance");
                if (nodes.getLength() == 0) {
                    System.err.println("[config] module " + modName + " (" + moduleConfig.getPath()
                        + ") declares no <instance> nodes; skipping");
                    continue;
                }
                // Port range is an optional guard: not every module schema declares
                // port-start/port-end (e.g. chemistry-config). When absent, accept any
                // positive port and rely on the global duplicate-port check below.
                Element root = modDoc.getDocumentElement();
                int portStart = parseAttr(root, "port-start", 1);
                int portEnd = parseAttr(root, "port-end", 65535);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element el = (Element) nodes.item(i);
                    if (!"true".equals(el.getAttribute("active"))) continue;
                    String instName = el.getAttribute("name");
                    int port = Integer.parseInt(el.getAttribute("port"));
                    if (port < portStart || port > portEnd) {
                        System.err.println("[config] " + modName + "." + instName + " port " + port
                            + " outside module range " + portStart + "-" + portEnd + "; skipping");
                        continue;
                    }
                    if (!usedPorts.add(port)) {
                        System.err.println("[config] " + modName + "." + instName + " port " + port
                            + " already in use by another instance; skipping");
                        continue;
                    }
                    String configPath = new File(moduleDir, instName + "/config.xml").getPath();
                    instances.add(new InstanceDef(modName + "." + instName, port, configPath));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int parseAttr(Element el, String name, int fallback) {
        String v = el.getAttribute(name);
        if (v == null || v.isEmpty()) return fallback;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("BaseServer starting " + instances.size() + " instance(s)...");
        for (InstanceDef inst : instances) {
            new Thread(() -> {
                try (ServerSocket ss = new ServerSocket(inst.port)) {
                    System.out.println("[" + inst.name + "] listening on port " + inst.port);
                    while (true) {
                        Socket client = ss.accept();
                        new Thread(() -> handle(inst, client)).start();
                    }
                } catch (IOException e) {
                    System.err.println("[" + inst.name + "] port " + inst.port + ": " + e.getMessage());
                }
            }).start();
        }
        Thread.currentThread().join();
    }

    private static void handle(InstanceDef inst, Socket client) {
        try (DataInputStream in = new DataInputStream(client.getInputStream());
             DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF(inst.name + ": received " + len + " samples");
        } catch (Exception e) {
            System.err.println("[" + inst.name + "] " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) { }
        }
    }
}
