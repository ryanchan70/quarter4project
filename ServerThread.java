import java.net.*;
import java.io.*;

public class ServerThread implements Runnable{
    private Socket clientSocket;
    public ServerThread(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void run(){
        System.out.println(Thread.currentThread().getName() + ": connection opened.");
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Sends a message
            out.println( "Connection Successful!");

            //Clears and close the output stream.
            out.flush();
            out.close();
            System.out.println(Thread.currentThread().getName() + ": connection closed.");
        } catch (IOException ex){
            System.out.println("Error listening for a connection");
            System.out.println(ex.getMessage());
        }
    }
}
