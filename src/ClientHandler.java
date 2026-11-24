import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = writer;
            this.username = in.readLine();
            if (this.username == null || this.username.isBlank()) {
                this.username = "Anonymous";
            }

            ChatServer.broadcast("[Server]: " + username + " joined the chat.");

            String message;
            while ((message = in.readLine()) != null) {
                if (!message.isBlank()) {
                    ChatServer.broadcast(username + ": " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("[Server]: Connection lost with " + (username != null ? username : "a client") + ".");
        } finally {
            ChatServer.removeClient(this);
            if (username != null) {
                ChatServer.broadcast("[Server]: " + username + " left the chat.");
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}