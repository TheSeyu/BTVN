import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server: lắng nghe kết nối từ nhiều Client thông qua ServerSocket.
 * Mỗi client kết nối vào sẽ được xử lý bởi một luồng (Thread) riêng biệt
 * là ClientHandler, cho phép server phục vụ đồng thời nhiều client
 * (mô hình client-server với Socket trong Java).
 */
public class Server {
    private static final int PORT = 8888;

    // Danh sách các client đang kết nối, dùng để broadcast tin nhắn
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("=== SERVER CHAT - Socket Java ===");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang chạy, lắng nghe tại cổng " + PORT + " ...");

            while (true) {
                // Chờ và chấp nhận kết nối mới từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới kết nối: " + clientSocket.getInetAddress().getHostAddress()
                        + ":" + clientSocket.getPort());

                ClientHandler handler = new ClientHandler(clientSocket, clients);
                clients.add(handler);

                // Mỗi client được xử lý trên một Thread riêng
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("Lỗi Server: " + e.getMessage());
        }
    }

    // Gửi tin nhắn tới tất cả client, trừ người gửi (nếu muốn loại trừ)
    static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }
}
