package net.osslabz.electrum;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A local Electrum stand-in: one JSON-RPC request per line in, one response per line out. */
class MockJsonRpcServer implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(MockJsonRpcServer.class);

    private final ServerSocket serverSocket;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Function<JsonNode, Object>> handlers = new ConcurrentHashMap<>();

    private final List<JsonNode> requests = new CopyOnWriteArrayList<>();

    private final Set<Socket> clientSockets = ConcurrentHashMap.newKeySet();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private volatile boolean running = true;

    MockJsonRpcServer() throws IOException {

        this.serverSocket = new ServerSocket(0, 0, InetAddress.getLoopbackAddress());
        executor.execute(this::acceptLoop);
    }

    String getHost() {

        return serverSocket.getInetAddress().getHostAddress();
    }

    int getPort() {

        return serverSocket.getLocalPort();
    }

    /** Answers {@code method} with the handler's return value as the result; other methods get "Method not found". */
    void handle(String method, Function<JsonNode, Object> handler) {

        handlers.put(method, handler);
    }

    List<JsonNode> requests() {

        return List.copyOf(requests);
    }

    private void acceptLoop() {

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                executor.execute(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    log.warn("IOException in accept loop: {}", e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {

        try (Socket socket = clientSocket;
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            String line;
            while (running && (line = reader.readLine()) != null) {
                JsonNode request = objectMapper.readTree(line);
                requests.add(request);
                writer.println(objectMapper.writeValueAsString(respondTo(request)));
            }
        } catch (IOException e) {
            if (running) {
                log.warn("Error handling client: {}", e.getMessage());
            }
        } finally {
            clientSockets.remove(clientSocket);
        }
    }

    private ObjectNode respondTo(JsonNode request) {

        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", request.get("id"));

        Function<JsonNode, Object> handler = handlers.get(request.get("method").asText());
        if (handler == null) {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("code", -32601);
            error.put("message", "Method not found");
            response.set("error", error);
        } else {
            response.set("result", objectMapper.valueToTree(handler.apply(request.get("params"))));
        }
        return response;
    }

    @Override
    public void close() throws IOException {

        running = false;
        serverSocket.close();
        for (Socket clientSocket : clientSockets) {
            clientSocket.close();
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
