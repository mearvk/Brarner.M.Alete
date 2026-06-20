import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class BaseServer
{
    private static final Map<String, Integer> ports = new HashMap<>();

    static {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new File("source-code/server-config.xml"));
            NodeList nodes = doc.getElementsByTagName("server");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                ports.put(el.getAttribute("name"), Integer.parseInt(el.getAttribute("port")));
            }
        } catch (Exception e) {
            ports.put("duke", 8000);
            ports.put("ncsu", 8001);
            ports.put("unc", 8002);
            ports.put("main", 8003);
        }
    }

    private static void listen(String name, ConnectionHandler handler) throws IOException {
        int port = ports.getOrDefault(name, 8000);
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[" + name + "] listening on port " + port);
            while (true) {
                Socket client = ss.accept();
                new Thread(() -> {
                    try { handler.handle(client); }
                    catch (Exception e) { e.printStackTrace(); }
                    finally { try { client.close(); } catch (IOException ignored) {} }
                }).start();
            }
        }
    }

    public static void duke() throws IOException {
        listen("duke", socket -> {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF("duke: received " + len + " samples");
        });
    }

    public static void ncsu() throws IOException {
        listen("ncsu", socket -> {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF("ncsu: received " + len + " samples");
        });
    }

    public static void unc() throws IOException {
        listen("unc", socket -> {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF("unc: received " + len + " samples");
        });
    }

    public static void main(String[] args) throws IOException {
        listen("main", socket -> {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int len = in.readInt();
            double[] data = new double[len];
            for (int i = 0; i < len; i++) data[i] = in.readDouble();
            out.writeUTF("main: received " + len + " samples");
        });
    }

    @FunctionalInterface
    interface ConnectionHandler {
        void handle(Socket socket) throws Exception;
    }
}
