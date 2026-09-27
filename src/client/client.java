package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Client: kết nối tới Server qua Socket.
 * Sử dụng 2 luồng:
 *  - Luồng chính: đọc dữ liệu nhập từ bàn phím và gửi lên server.
 *  - Luồng phụ (ListenThread): liên tục lắng nghe tin nhắn từ server để in ra màn hình.
 */
public class client {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("Đã kết nối tới server " + SERVER_HOST + ":" + SERVER_PORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Nhập tên của bạn: ");
            String name = scanner.nextLine();
            out.println(name);

            // Luồng riêng để lắng nghe tin nhắn từ server (broadcast từ các client khác)
            Thread listenThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Mất kết nối tới server.");
                }
            });
            listenThread.setDaemon(true);
            listenThread.start();

            System.out.println("Bắt đầu chat (gõ 'exit' để thoát):");
            String message;
            while (true) {
                message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
                out.println(message);
            }
        } catch (UnknownHostException e) {
            System.out.println("Không tìm thấy host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
