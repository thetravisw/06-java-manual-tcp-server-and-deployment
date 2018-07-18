import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionHandler implements Runnable {
    private User user;

    public ConnectionHandler(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        try {
            handleMessage();
        } catch (IOException e) {

        }
    }

    public void handleMessage() throws IOException {
        InputStream inputStream = this.user.socket.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(reader);

        OutputStream outputStream = this.user.socket.getOutputStream();
        DataOutputStream backToClient = new DataOutputStream(outputStream);

        boolean isRunning = true;
        while (isRunning) {
            String line = buffer.readLine();
            String response = line.toUpperCase() + "\n";

            if (line.startsWith("@quit")) {
                isRunning = false;
                this.user.socket.close();
            } else if (line.startsWith("@list")) {
                response = listUsers(line);
            } else if (line.startsWith("@nickname")) {
                nickname(line);
            } else if (line.startsWith("@dm")) {
                dm(line);
            }

            // TODO: implement other command methods

            TCPServer.broadcast(this.user.toString() + ": " + response);
        }
    }
    public void dm (String line) {
        Scanner linescanner = new Scanner(line);
        linescanner.next();
        String MessageGoesTo = linescanner.next();

        String dm = "Direct Message from " + this.user.nickname +":";
        while (linescanner.hasNext()) {
            dm += " " + linescanner.next();
        }
        TCPServer.dm(dm, MessageGoesTo);
    }

    public void nickname (String line){
        Scanner linescanner = new Scanner(line);
        String nickname = linescanner.next();
        nickname = linescanner.next();
        this.user.nickname= nickname;
    }

    public String listUsers (String line) {
        String response = "";

        for (User user : TCPServer.connections) {
            response += user.toString() + "\n";
        }

        return response;
    }
}
