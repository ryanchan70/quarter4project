import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientScreen extends JPanel implements ActionListener {
    private Client client;
    private JButton startButton;
    private int numReady, numClients;
    private boolean ready, startGame;
    private Obstacle[] obstacles;

    public ClientScreen() {
        startGame = false;
        startButton = new JButton("Start");
        startButton.setBounds(350, 500, 100, 30);
        add(startButton);
        startButton.addActionListener(this);

        ready = false;

        obstacles = new Obstacle[3];
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = new Obstacle();
        }

        setLayout(null);
        setFocusable(true);
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!startGame) {
            g.setColor(new Color(38, 41, 45));
            g.fillRect(0, 0, 800, 600);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 40));
            g.drawString("Welcome!", 300, 85);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.drawString("Please wait for all players to press start.", 170, 175);
            g.drawString(numReady + "/" + numClients + " players ready", 300, 250);
        } else {
            g.setColor(new Color(52, 168, 117));
            for (int i = 0; i < obstacles.length; i++) {
                g.fillRect(obstacles[i].getX(), obstacles[i].getY(), obstacles[i].getWidth(), obstacles[i].getHeight());
                System.out.println(obstacles[i].getX() + " " + obstacles[i].getY());
            }
        }
        System.out.println("clientscreen: " + numReady + " " + numClients);
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            ready = !ready;
            setReady();
        }
    }

    public void updateReadyMessage(int numReady, int numClients) {
        this.numReady = numReady;
        this.numClients = numClients;
        if(numReady == numClients) {
            startGame = true;
            client.send("STARTGAME"); //tells the serverthread to start game
            startButton.setVisible(false);
        }
        repaint();
    }

    public void updateObstacles(String[] obstacleInfo) {
        // expected input format: obstacle1X obstacle1Y obstacle2X obstacle2Y, etc.
        for (int i = 0; i < obstacleInfo.length; i += 2) {
            obstacles[i/2].setX(Integer.parseInt(obstacleInfo[i]));
            obstacles[i/2].setY(Integer.parseInt(obstacleInfo[i+1]));
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setReady() {
        if (client != null) {
            client.setReady(ready);
            if(ready) {
                client.send("READY");
            } else {
                client.send("NOTREADY");
            }
        }
    }
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
