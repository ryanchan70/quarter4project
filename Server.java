import java.net.*;
import java.io.*;
import java.util.List;

public class Server {
    private static DLList<ServerThread> clientThreads = new DLList<>();
    private static int readyCount = 0;
    private static int clientCount = 0;
    private static int remainingPlayers = 0;
    private static volatile boolean gameStart = false;
    private static Obstacle[] obstacles = {new Obstacle(300,370,15,30),
                                            new Obstacle(500,370,15,30),
                                            new Obstacle(800,370,15,30)};
    private static MyHashMap<Integer, Integer> scores = new MyHashMap<>();
    private static Powerup powerup = new Powerup(900,370,15,30);
    private static boolean powerUpOn = false;
    private static int powerUpPlayer = -1;

    public Server() {

    }

    public static void main(String[] args) throws IOException {
        int portNumber = 1024;
        ServerSocket serverSocket = new ServerSocket(portNumber);

        Thread clientAcceptanceThread = new Thread(() -> {
            try {
                while (!gameStart) {
                    System.out.println("gameStart is " + gameStart);
                    System.out.println("Waiting for a connection");

                    try {
                        Socket clientSocket = serverSocket.accept();
                        ServerThread thread = new ServerThread(clientSocket, clientCount);
                        clientThreads.add(thread);
                        thread.start();
                        clientCount++;

                    } catch (SocketTimeoutException ignored) {
                    }

                    // Broadcast the updated client count and ready count to all clients
                    broadcastReadyMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientAcceptanceThread.start();



        // Wait for the game to start
        while (!gameStart) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("waiting for a connection");
        }

        System.out.println("GAME HAS STARTED");

        // stop accepting clients when game has started
        clientAcceptanceThread.interrupt();

        // Start the game logic
        remainingPlayers = clientCount;

        while (gameStart) {
            try {
                Thread.sleep(20);

                for (int i = 0; i < 3; i++) {
                    obstacles[i].setX(obstacles[i].getX() - 5);
                    if(obstacles[i].getX() < -obstacles[i].getWidth()) {
                        obstacles[i].setX(900);
                    }
                }

                broadcastObstacleMessage();

                if ((int)(Math.random()*200)+1 == 1 && !powerUpOn){
                    System.out.println("POWERUP");
                    powerUpOn = true;
                    powerUpPlayer = (int)(Math.random()*clientThreads.getSize());
                }
                if (powerUpOn){
                    powerup.setX(powerup.getX()-5);
                    if (powerup.getX() < -powerup.getWidth()){
                        powerup.setX(800);

                        powerUpOn = false;
                    }
                    broadcastPowerUpMessage();
                }

                if (remainingPlayers == 0) {
                    endGame();
                    gameStart = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeClientThread(ServerThread thread) {
        clientThreads.remove(thread); // Remove the thread from the list
        // Decrement readyCount if the client had pressed the "ready" button
        if (thread.isReady()) {
            readyCount--;
        }
        clientCount--;
        if (gameStart) {
            remainingPlayers--;
        }
        // Broadcast the updated client count and ready count to all clients
        broadcastReadyMessage();
    }

    public static void broadcastReadyMessage() {
        int clientCount = clientThreads.getSize();
        String msg = "READY " + readyCount + " " + clientCount;
        if (readyCount == clientCount){
            gameStart = true;
        }
        for (int i = 0; i < clientThreads.getSize(); i++){
            clientThreads.get(i).sendMessage(msg);
        }
        System.out.println("server -> serverthread : " + msg);
    }

    public static void playerLoses(int id, int score) {
        scores.put(id, score);
        remainingPlayers--;
        for (int i = 0; i < clientThreads.getSize(); i++) {
            System.out.println("threadid vs id: " + clientThreads.get(i).getID() + " " + id);
            if (clientThreads.get(i).getID() != id) {
                clientThreads.get(i).sendMessage("PLAYERLOST " + id + " " + score);
            }
        }
    }

    public static void activatePowerup(int id) {
        powerUpOn = false;
        int effect = (int) (Math.random()*4); // larger obstacles, 2x score multiplier, g-belt, invincibility
        if (effect == 0) {
            for (int i = 0; i < clientThreads.getSize(); i++){
                //change this
                if (i != id) {
                    clientThreads.get(i).sendMessage("INCREASEHEIGHT");
                }
            }
            //not implemented yet
        } else if (effect == 1) {
            clientThreads.get(id).sendMessage("SCOREMULTIPLIER");
        } else if (effect == 2) {
            clientThreads.get(id).sendMessage("GRAVITY");
        } else if (effect == 3) {
            clientThreads.get(id).sendMessage("INVINCIBILITY");
        }
    }

    public static void endGame() {
        System.out.println(scores);
        int highestScore = 0;
        int winnerID = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) > highestScore) {
                highestScore = scores.get(i);
                winnerID = i;
            }
        }
        for (int i = 0; i < clientThreads.getSize(); i++){
            clientThreads.get(i).sendMessage("GAMEOVER " + winnerID + " " + highestScore);
        }
    }

    public static void broadcastObstacleMessage(){
        for (int j = 0; j < clientThreads.getSize(); j++) {
            String msg = "OBSTACLES";
            for (int i = 0; i < obstacles.length; i++) {
                msg += " " + obstacles[i].getX() + " " + obstacles[i].getY();
            }
            clientThreads.get(j).sendMessage(msg);
        }
    }

    public static void broadcastPowerUpMessage(){
        if (clientThreads.get(powerUpPlayer).isAlive()) {
            clientThreads.get(powerUpPlayer).sendMessage("POWERUP " + powerup.getX() + " " + powerup.getY());
        }
    }

    public static void incrementReadyCount() {
        readyCount++;
        broadcastReadyMessage();
    }

    public static void decrementReadyCount() {
        readyCount--;
        broadcastReadyMessage();
    }

    public static void setGameStart(boolean gameStart) {
        Server.gameStart = gameStart;
        System.out.println("GAMESTART was set to " + Server.gameStart);
    }
}

