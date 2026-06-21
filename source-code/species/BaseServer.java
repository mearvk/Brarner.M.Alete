package species;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Base server for species/animalia signal processing instances.
 * Reads port range and active instances from species/config.xml.
 */
public class BaseServer
{
    private static int portStart, portEnd;
    private static final List<String[]> activeInstances = new ArrayList<>();

    static {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new File("source-code/species/config.xml"));
            Element root = (Element) doc.getElementsByTagName("module-config").item(0);
            portStart = Integer.parseInt(root.getAttribute("port-start"));
            portEnd = Integer.parseInt(root.getAttribute("port-end"));
            NodeList nodes = doc.getElementsByTagName("instance");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                if ("true".equals(el.getAttribute("active"))) {
                    int port = Integer.parseInt(el.getAttribute("port"));
                    if (port >= portStart && port <= portEnd) {
                        activeInstances.add(new String[]{el.getAttribute("name"), String.valueOf(port)});
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("[species] port range: " + portStart + "-" + portEnd);
        for (String[] inst : activeInstances) {
            String name = inst[0];
            int port = Integer.parseInt(inst[1]);
            new Thread(() -> {
                try (ServerSocket ss = new ServerSocket(port)) {
                    System.out.println("[species." + name + "] listening on port " + port);
                    while (true) {
                        Socket client = ss.accept();
                        new Thread(() -> handle(name, client)).start();
                    }
                } catch (IOException e) { e.printStackTrace(); }
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
            out.writeUTF("species." + name + ": received " + len + " samples");
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { client.close(); } catch (IOException ignored) {} }
    }
}
