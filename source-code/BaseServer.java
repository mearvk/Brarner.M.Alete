import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Master BaseServer — reads source-code/config.xml to determine which modules
 * and instances are active, then starts server threads on the configured ports.
 */
public class BaseServer
{
    private static final List<String[]> instances = new ArrayList<>();

    static {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new File("source-code/config.xml"));
            NodeList modules = doc.getElementsByTagName("module");
            for (int m = 0; m < modules.getLength(); m++) {
                Element mod = (Element) modules.item(m);
                if (!"true".equals(mod.getAttribute("enabled"))) continue;
                String modName = mod.getAttribute("name");
                String configPath = mod.getAttribute("config");
                // Read the module's config.xml for active instances
                Document modDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new File(configPath));
                Element root = (Element) modDoc.getElementsByTagName("module-config").item(0);
                int portStart = Integer.parseInt(root.getAttribute("port-start"));
                int portEnd = Integer.parseInt(root.getAttribute("port-end"));
                NodeList nodes = modDoc.getElementsByTagName("instance");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element el = (Element) nodes.item(i);
                    if ("true".equals(el.getAttribute("active"))) {
                        int port = Integer.parseInt(el.getAttribute("port"));
                        if (port >= portStart && port <= portEnd) {
                            instances.add(new String[]{modName + "." + el.getAttribute("name"), String.valueOf(port)});
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("BaseServer starting " + instances.size() + " instance(s)...");
        for (String[] inst : instances) {
            String name = inst[0];
            int port = Integer.parseInt(inst[1]);
            new Thread(() -> {
                try (ServerSocket ss = new ServerSocket(port)) {
                    System.out.println("[" + name + "] listening on port " + port);
                    while (true) {
                        Socket client = ss.accept();
                        new Thread(() -> handle(name, client)).start();
                    }
                } catch (IOException e) { System.err.println("[" + name + "] port " + port + ": " + e.getMessage()); }
            }).start();
        }
        Thread.currentThread().join();
    }

    private static void handle(String name, Socket client) {
        try (DataInputStream in = new DataInputStream(client.getInputStream());
             DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF(name + ": received " + len + " samples");
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { client.close(); } catch (IOException ignored) {} }
    }
}
