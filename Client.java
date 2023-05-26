import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JPanel {
    private String serverAddress;
    private int serverPort, id;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ClientScreen clientScreen;
    private boolean ready;
    private static int restartCount = 0;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientScreen = new ClientScreen();
        clientScreen.setClient(this);
        ready = false;
    }

    public void updateReadyMessage(int numReady, int numClients) {
        clientScreen.updateReadyMessage(numReady, numClients);
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 1024;
        Client client = new Client(serverAddress, serverPort);
        client.connect();

        client.startClientScreen();

        // Start receiving updates from the server in a separate thread
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = client.reader.readLine()) != null) {

                    // SERVER RESPONSE HANDLING
                    // System.out.println("serverthread -> client: " + response);
                    if (response.startsWith("ID")) {
                        response = response.substring(3);
                        client.clientScreen.setId(Integer.parseInt(response));

                    } else if (response.startsWith("READY")) {
                        response = response.substring(6);
                        String[] info = response.split("\\s+");
                        client.clientScreen.updateReadyMessage(Integer.parseInt(info[0]), Integer.parseInt(info[1]));

                    } else if (response.startsWith("OBSTACLES")) {
                        response = response.substring(10);
                        String[] info = response.split("\\s+");
                        client.clientScreen.updateObstacles(info);

                    } else if(response.startsWith("POWERUP")) {
                        response = response.substring(8);
                        String[] info = response.split("\\s+");
                        client.clientScreen.updatePowerUp(info);
                    } else if(response.equals("SCOREMULTIPLIER") || response.startsWith("GRAVITY") || response.startsWith("INVINCIBILITY")) {
                        client.clientScreen.activatePowerup(response);
                    } else if (response.startsWith("PLAYERLOST")) {
                        response = response.substring(11);
                        String[] info = response.split("\\s+");
                        client.clientScreen.updateOpponentStatus(Integer.parseInt(info[0]), Integer.parseInt(info[1]));

                    } else if (response.startsWith("GAMEOVER")){
                        response = response.substring(9);
                        String[] info = response.split("\\s+");
                        client.clientScreen.gameOver(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
                    } else if (response.startsWith("RESTART")) {
                        restartCount++;
                    } else {
                        System.out.println("serverthread -> client: " + response);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();

        // Wait for the receive thread to complete (e.g., if you want to stop receiving updates)
        // You can remove this part if you want the receive thread to run indefinitely
        try {
            receiveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.disconnect();
    }

    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + socket);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //client -> serverthread
    public void send(String message) {
        writer.println(message);
        System.out.println("client -> serverthread: " + message);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startClientScreen() {
        JFrame frame = new JFrame("Quarter 4 Project");
        frame.add(clientScreen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
