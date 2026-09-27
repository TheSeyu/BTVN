import java.io.*;
import java.net.*;
import java.util.*;

/**
 * ClientHandler: chạy trên một Thread riêng cho mỗi client.
 * Nhiệm vụ: đọc dữ liệu client gửi lên và broadcast cho các client khác,
 * đồng thời gửi dữ liệu (nếu server chủ động gửi) xuống cho client này.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Dòng đầu tiên client gửi lên được quy ước là tên đăng nhập
            clientName = in.readLine();
            System.out.println(clientName + " đã tham gia phòng chat.");
            Server.broadcast(clientName + " đã tham gia phòng chat.", this);

            String line;
            while ((line = in.readLine()) != null) {
                String message = clientName + ": " + line;
                System.out.println(message);
                Server.broadcast(message, this);
            }
        } catch (IOException e) {
            System.out.println("Client " + clientName + " ngắt kết nối đột ngột.");
        } finally {
            try {
                Server.removeClient(this);
                Server.broadcast(clientName + " đã rời phòng chat.", this);
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    void sendMessage(String message) {
        out.println(message);
    }
}
