import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean ready;
    private int id;

    public ServerThread(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.ready = false;
        this.id = id;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Sends a message
            sendMessage("ID " + id);

            // Continuously listen for client messages
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println(Thread.currentThread().getName() + ": received message: " + clientMessage);
                // Process client messages here
                System.out.println("serverthread receives: " + clientMessage);
                if (clientMessage.equals("READY")) {
                    ready = true;
                    Server.incrementReadyCount();
                } else if (clientMessage.equals("NOTREADY")) {
                    ready = false;
                    Server.decrementReadyCount();
                } else if (clientMessage.equals("STARTGAME")) {
                    Server.setGameStart(true);
                } else if (clientMessage.startsWith("COLLISION")) {
                    int score = Integer.parseInt(clientMessage.substring(10));
                    Server.playerLoses(id, score);
                } else if (clientMessage.equals("POWERUPCOLLECTED")){
                    // TODO: Apply powerup effect
                    Server.activatePowerup(id);
                }
            }

            in.close();
            out.close();
            clientSocket.close();

            // Notify the server that this client thread is being removed
            Server.removeClientThread(this);
            System.out.println(Thread.currentThread().getName() + ": connection closed.");
        } catch (IOException ex) {
            System.out.println("Error listening for a connection");
            System.out.println(ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    public boolean isReady() {
        return ready;
    }

    public int getID() {
        return id;
    }
}
