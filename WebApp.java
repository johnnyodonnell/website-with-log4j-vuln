import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class WebApp {

    private final static Logger logger = LogManager.getLogger();

    private static final String index = getHtml("index");

    private static final String success = getHtml("success");

    private static String getHtml(String fileName) {
        try {
            return new String(Files.readAllBytes(
                        Paths.get("html/" + fileName + ".html")));
        } catch (IOException e) {
            logger.error("Could not open file {}", fileName);
            System.exit(9);
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        int port = 8010;
        String stringPort = System.getenv("COOKIES_PORT");
        if (stringPort != null) {
            port = new Integer(stringPort);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new IndexHandler());
        server.start();
        logger.error("Server started...");
        logger.error("Running on OS: ${java:os}");
    }

    static class IndexHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;

            if (exchange.getRequestMethod().equals("GET")) {
                response = index;
            } else /* Assume post */ {
                Scanner scanner =
                    new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
                String requestBody = scanner.hasNext() ? scanner.next() : "";

                String[] params =
                    URLDecoder.decode(
                            requestBody,
                            StandardCharsets.UTF_8.name())
                        .split("\\&");

                String name = getValue(params[0]);
                String qty = getValue(params[1]);
                String cc = getValue(params[2]);

                String entry = String.join(",", name, qty, cc) + "\n";

                String filename = "database.csv";

                // Create file if it does not already exist
                new File(filename).createNewFile();

                Files.write(
                        Paths.get(filename),
                        entry.getBytes(),
                        StandardOpenOption.APPEND);

                logger.error(name + " just ordered " + qty + " cookies.");

                response = success;
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static String getValue(String param) {
        int equalIndex = param.indexOf("=");
        return param.substring(equalIndex + 1).replace("+", " ");
    }
}

