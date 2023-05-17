import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ServerThread> clientThreads = new ArrayList<>();
    private static int readyCount = 0;
    private static int clientCount = 0;

    public static void main(String[] args) throws IOException {
        int portNumber = 1024;
        ServerSocket serverSocket = new ServerSocket(portNumber);

        // This loop will run and wait for one connection at a time.
        while (true) {
            System.out.println("Waiting for a connection");

            // Wait for a connection.
            Socket clientSocket = serverSocket.accept();

            // Once a connection is made, run the socket in a ServerThread.
            ServerThread thread = new ServerThread(clientSocket);
            clientThreads.add(thread); // Add the thread to the list
            thread.start();
            clientCount++;

            // Broadcast the updated client count and ready count to all clients
            broadcastReadyMessage();
        }
    }

    public static void removeClientThread(ServerThread thread) {
        clientThreads.remove(thread); // Remove the thread from the list
        // Decrement readyCount if the client had pressed the "ready" button
        if (thread.isReady()) {
            readyCount--;
        }
        // Broadcast the updated client count and ready count to all clients
        broadcastReadyMessage();
    }

    public static void broadcastReadyMessage() {
        int clientCount = clientThreads.size();
        String msg = "READY " + readyCount + " " + clientCount;
        for (ServerThread thread : clientThreads) {
            thread.sendMessage(msg);
        }
        System.out.println("server -> serverthread : " + msg);
    }

    public static void incrementReadyCount() {
        readyCount++;
        broadcastReadyMessage();
    }

    public static void decrementReadyCount() {
        readyCount--;
        broadcastReadyMessage();
    }
}
