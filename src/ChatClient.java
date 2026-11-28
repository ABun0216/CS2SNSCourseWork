import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int PORT = 8888;

    public static void main(String[] args) {
        String serverHost = (args.length > 0) ? args[0] : DEFAULT_HOST;

        try (
            Socket socket = new Socket(serverHost, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();
            out.println(username);

            // Background thread listens for incoming server broadcasts
            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            System.out.println("Connected! Type your message and press Enter (type /quit to exit):");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if ("/quit".equalsIgnoreCase(input.trim())) {
                    break;
                }
                out.println(input);
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}