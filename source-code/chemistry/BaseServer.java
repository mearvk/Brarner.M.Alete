package chemistry;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class BaseServer
{
    private static final List<String[]> activeInstances = new ArrayList<>();

    static {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new File("source-code/chemistry/config.xml"));
            Element moduleEl = (Element) doc.getElementsByTagName("module").item(0);
            if (!"true".equals(moduleEl.getAttribute("active"))) {
                System.out.println("[chemistry] module disabled in config");
            } else {
                NodeList nodes = doc.getElementsByTagName("instance");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element el = (Element) nodes.item(i);
                    if ("true".equals(el.getAttribute("active")))
                        activeInstances.add(new String[]{el.getAttribute("name"), el.getAttribute("port")});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("[chemistry] starting " + activeInstances.size() + " instance(s)");
        for (String[] inst : activeInstances) {
            String name = inst[0];
            int port = Integer.parseInt(inst[1]);
            new Thread(() -> {
                try (ServerSocket ss = new ServerSocket(port)) {
                    System.out.println("[chemistry." + name + "] listening on port " + port);
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
        try (DataInputStream in   = new DataInputStream(client.getInputStream());
             DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF("chemistry." + name + ": received " + len + " samples");
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { client.close(); } catch (IOException ignored) {} }
    }
}
