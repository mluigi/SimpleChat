package pr0.ves.simplechat.Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

class ServerWeb implements MessageListener {
    private HttpServer server;
    private final StringBuilder message = new StringBuilder();

    ServerWeb() {
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.createContext("/", httpExchange -> {
            String response = "<html><head><title>Chat Log</title></head><body>" +
                    this.message +
                    "</body></html>";

            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(Executors.newCachedThreadPool());

    }

    @Override
    synchronized public void processMessage(ServerThread serverThread, String message) {
        if (message.startsWith("MESSAGE")) {
            this.message.append("<p>").append(serverThread.getClientData().getName()).append(": ").append(message.split("=", 2)[1]).append("</p>\n");
        } else if (message.startsWith("CHECKNAME") || message.startsWith("NAMEANDADDRESS")) {
            if (serverThread.isChecked()) {
                this.message.append("<p>").append(serverThread.getClientData().getName()).append(" is connecting.").append("</p>\n");
            }
        } else if (message.startsWith("EXIT")) {
            this.message.append("<p>").append(serverThread.getClientData().getName()).append(" left.").append("</p>\n");
        }
    }
}
