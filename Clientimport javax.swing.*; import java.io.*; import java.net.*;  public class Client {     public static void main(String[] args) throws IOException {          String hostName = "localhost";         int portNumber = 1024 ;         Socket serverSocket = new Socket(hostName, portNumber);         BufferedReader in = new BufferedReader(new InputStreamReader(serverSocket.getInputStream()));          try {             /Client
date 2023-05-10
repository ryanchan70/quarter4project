import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        String hostName = "localhost";
        int portNumber = 1024 ;
        Socket serverSocket = new Socket(hostName, portNumber);
        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

        try {
            //Waiting for input from the server
            String fromServer = in.readLine();
            System.out.println("Server: " + fromServer);


            JFrame frame = new JFrame("Game");
            ClientScreen screen = new ClientScreen();
            frame.add(screen);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error connecting to " + hostName);
            System.exit(1);
        }
    }
}
