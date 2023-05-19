import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ClientScreen extends JPanel implements ActionListener, KeyListener {
    private Client client;
    private JButton readyButton;
    private int numReady, numClients, jumpStrength;
    private boolean ready, startGame, gameOver, jumping;
    private Obstacle[] obstacles;
    private Player player;
    private String opponentLog; // to display opponents who lost(and their score when that's implemented)

    public ClientScreen() {
        startGame = false;
        readyButton = new JButton("Ready");
        readyButton.setBounds(350, 500, 100, 30);
        add(readyButton);
        readyButton.addActionListener(this);

        ready = false;
        gameOver = false;

        obstacles = new Obstacle[3];
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = new Obstacle();
        }
        player = new Player();

        jumpStrength = 0;
        jumping = false;

        opponentLog = "";

        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!startGame) {
            if (!gameOver) {
                g.setColor(new Color(38, 41, 45));
                g.fillRect(0, 0, 800, 600);
                g.setColor(Color.white);
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                g.drawString("Welcome!", 300, 85);
                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                g.drawString("Please wait for all players to press start.", 170, 175);
                g.drawString(numReady + "/" + numClients + " players ready", 300, 250);
            } else {
                g.setColor(new Color(38, 41, 45));
                g.fillRect(0, 0, 800, 600);
                g.setColor(Color.white);
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                g.drawString("You lost...", 295, 85);
                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                g.drawString("Better luck next time!", 275, 175);
                //g.drawString(numReady + "/" + numClients + " players ready", 300, 250);
            }
        } else {
            g.setColor(new Color(229, 92, 62));
            g.fillRect(0, 0, 800, 600);
            g.setColor(new Color(141, 74, 26));
            g.fillRect(0, 400, 800, 200);
            g.setColor(new Color(52, 168, 117));
            for (int i = 0; i < obstacles.length; i++) {
                g.fillRect(obstacles[i].getX(), obstacles[i].getY(), obstacles[i].getWidth(), obstacles[i].getHeight());
            }
            g.setColor(new Color(53, 155, 229));
            g.fillRect(player.getX(), player.getY(), 30, 30);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            drawString(g, opponentLog, 650, 50);

            if (jumping) {
                //System.out.println("jumping, jump strength = " + jumpStrength);
                player.setY(player.getY() - jumpStrength); // Move the player on the y-axis based on the strength of the jump.
                jumpStrength -= 2; // Gradually decrease the strength of the jump.

                if (player.getY() > 400 - player.getHeight()) {
                    player.setY(400 - player.getHeight()); // Ensure the player does not fall through the floor.
                    jumpStrength = 0;
                    jumping = false;
                }
            }

            for (int i = 0; i < obstacles.length; i++) {
                if (player.checkCollision(obstacles[i].getX(), obstacles[i].getY(), obstacles[i].getWidth(), obstacles[i].getHeight())) {
                    client.send("COLLISION");
                    startGame = false;
                    gameOver = true;
                    break;
                }
            }
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {}

        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == readyButton) {
            if (ready) {
                readyButton.setText("Ready");
            } else {
                readyButton.setText("Cancel");
            }
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
            readyButton.setVisible(false);
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

    public void updateOpponentStatus(int id) {
        opponentLog += "Player " + id + " has lost.\n";
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
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(player.getY() + " vs " + (400-player.getHeight()));
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP && player.getY() >= 400-player.getHeight()) { // Must be on the ground to jump.
            jumpStrength = 24; // upwards velocity
            jumping = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // custom drawString that supports new line characters
    public void drawString(Graphics g, String text, int x, int y) {
        int lineHeight = g.getFontMetrics().getHeight();
        for (String line : text.split("\n"))
            g.drawString(line, x, y += lineHeight);
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
