// connect to this server using telnet:
// telnet localhost 6789

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class TCPServer {
    public static List<User> connections = new ArrayList<>();

    // send a message to all open connections
    // stretch-TODO: prevent messages from being broadcast to the same user
    // that sent them.
    public static void broadcast(String message) {
        for (User user : connections) {
            try {
                DataOutputStream outToClient = new DataOutputStream(user.socket.getOutputStream());
                outToClient.writeBytes(message);
            } catch (IOException e) {

            }
        }
    }

    public static void dm (String message, String dmTarget){
        for (User user : connections){
            if (user.nickname == dmTarget){
                try {
                    DataOutputStream dmToUser = new DataOutputStream(user.socket.getOutputStream());
                    dmToUser.writeBytes(message);
                } catch (IOException e) {}
            }
        }
    }

    public static void main(String argv[]) throws Exception {
        startServer();
    }

    public static void startServer() {
        int port = getPort();

        System.out.println("Connecting to port " + port);
        try (ServerSocket welcomeSocket = new ServerSocket(port)) {
            boolean isRunning = true;
            while (isRunning) {
                System.out.print("Waiting for connection... ");
                Socket connectionSocket = welcomeSocket.accept();

                User user = new User("unknown", connectionSocket);

                ConnectionHandler connection = new ConnectionHandler(user);
                (new Thread(connection)).start();

                connections.add(user);
                System.out.println("connection received!");

            }
        } catch (IOException e) {

        }
    }


    public static int getPort() {
        int defaultPort = 6790;

        String portEnv = System.getenv("USER");
        if (portEnv != null) {
            try {
                return Integer.valueOf(System.getenv("PORT"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid port: " + portEnv);
            }
        }
        return defaultPort;
    }
}
