import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executorService;
    private final List<String> validPaths = List.of(
            "/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html",
            "/events.html", "/events.js"
    );
    private static final int PORT = 8080;
    private static final int THREAD_POOL = 64;

    private final ConcurrentHashMap<String, Map<String, Handler>> handlerHashMap;

    public Server() {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL);
        handlerHashMap = new ConcurrentHashMap<>();
    }

    public void run() {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                executorService.submit(() -> prepare(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare(Socket socket) {
        try (
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            final var requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            String method = parts[0];
            String path = parts[1];

            // Обработка корневого пути
            if (path.equals("/")) {
                path = "/index.html";
            }

            Request request = new Request(method, path);

            // Получаем путь без параметров для поиска в handlerHashMap
            String pathWithoutParams = preparePath(path);

            // Сначала проверяем, есть ли зарегистрированный handler
            if (handlerHashMap.containsKey(method)) {
                Map<String, Handler> handlerMap = handlerHashMap.get(method);
                if (handlerMap.containsKey(pathWithoutParams)) {
                    Handler handler = handlerMap.get(pathWithoutParams);
                    handler.handle(request, out);
                    return; // Важно: выходим после обработки
                }
            }

            // Если handler не найден, проверяем статические файлы
            if (validPaths.contains(pathWithoutParams)) {
                defaultHandle(out, pathWithoutParams);
            } else {
                customReponse(out, 404, "Not found");
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void defaultHandle(BufferedOutputStream out, String path) throws IOException {
        final var filePath = Path.of("public", path);
        final var mimeType = Files.probeContentType(filePath);
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }
        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public void customReponse(BufferedOutputStream out, int code, String status) throws IOException {
        out.write((
                "HTTP/1.1 " + code + " " + status + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlerHashMap.containsKey(method)) {
            handlerHashMap.put(method, new HashMap<>());
        }
        handlerHashMap.get(method).put(path, handler);
    }

    public String preparePath(String url) {
        int i = url.indexOf("?");
        if (i == -1) {
            return url;
        }
        return url.substring(0, i);
    }
}
