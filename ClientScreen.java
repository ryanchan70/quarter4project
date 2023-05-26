import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ClientScreen extends JPanel implements ActionListener, KeyListener {
    private Client client;
    private JButton readyButton;
    private int id, numReady, numClients, jumpStrength, score, winnerScore, scoreMultiplier;
    private boolean ready, startGame, gameOver, jumping, winner, powerUpActivated, gravity, invincibility;
    private Obstacle[] obstacles;
    private Powerup powerUp;
    private Player player;
    private String powerupLog, opponentLog;

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
        powerUp = new Powerup();

        jumpStrength = 0;
        jumping = false;
        winner = false;
        powerupLog = "";
        opponentLog = "";
        score = 0;

        scoreMultiplier = 1;
        gravity = false;
        invincibility = false;

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
                g.drawString("Please wait for all players to press start.", 180, 175);
                if (ready) {
                    g.setColor(new Color(61, 152, 49));
                }
                g.drawString(numReady + "/" + numClients + " players ready", 302, 250);
            } else {
                if (winner) {
                    g.setColor(new Color(183, 151, 0));
                    g.fillRect(0, 0, 800, 600);
                    g.setColor(new Color(255, 255, 255));
                    g.setFont(new Font("SansSerif", Font.BOLD, 40));
                    g.drawString("You won!!!", 295, 105);
                    g.setFont(new Font("SansSerif", Font.BOLD, 20));
                    g.drawString("Congratulations, you got the highest score!", 178, 165);
                    g.drawString("Your final score: " + score, 285, 300);
                } else {
                    g.setColor(new Color(38, 41, 45));
                    g.fillRect(0, 0, 800, 600);
                    g.setColor(Color.white);
                    g.setFont(new Font("SansSerif", Font.BOLD, 40));
                    g.drawString("You died...", 295, 105);
                    g.setFont(new Font("SansSerif", Font.BOLD, 20));
                    g.drawString("Your final score: " + score, 285, 300);
                    if (winnerScore != 0) {
                        g.drawString("Winner's score: " + winnerScore, 295, 350);
                        g.drawString("Better luck next time!", 285, 165);
                    } else {
                        g.drawString("Waiting for other players to finish...", 205, 165);
                    }
                }
            }
        } else {
            //temporary basic background
            g.setColor(new Color(229, 92, 62));
            g.fillRect(0, 0, 800, 600);
            g.setColor(new Color(141, 74, 26));
            g.fillRect(0, 400, 800, 200);
            g.setColor(new Color(52, 168, 117));
            for (int i = 0; i < obstacles.length; i++) {
                g.fillRect(obstacles[i].getX(), obstacles[i].getY(), obstacles[i].getWidth(), obstacles[i].getHeight());
            }
            if (powerUpActivated) {
                g.setColor(new Color(255, 147, 147));
                g.fillRect(powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight());
            }
            if (invincibility) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(234, 124, 216));
                g2.drawOval(player.getX() - 10, player.getY() - 10, player.getWidth() + 20, player.getHeight() + 20);
            }
            g.setColor(new Color(53, 155, 229));
            g.fillRect(player.getX(), player.getY(), 30, 30);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            score += (2 * scoreMultiplier);
            g.drawString("Score: " + score, 660, 30);
            g.drawString(powerupLog, 400, 30);
            drawString(g, opponentLog, 630, 60);

            if (jumping) {
                player.setY(player.getY() - jumpStrength); // Move the player on the y-axis based on the strength of the jump.
                jumpStrength -= 2; // Gradually decrease the strength of the jump.

                if (player.getY() > 400 - player.getHeight()) {
                    player.setY(400 - player.getHeight()); // Ensure the player does not fall through the floor.
                    jumpStrength = 0;
                    jumping = false;
                }
            }

            if (!invincibility) {
                for (int i = 0; i < obstacles.length; i++) {
                    if (player.checkCollision(obstacles[i].getX(), obstacles[i].getY(), obstacles[i].getWidth(), obstacles[i].getHeight())) {
                        client.send("COLLISION " + score);
                        startGame = false;
                        gameOver = true;
                        break;
                    }
                }
            }

            if (powerUpActivated) {
                if (player.checkCollision(powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight())) {
                    client.send("POWERUPCOLLECTED");
                    powerUpActivated = false;
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

    public void updatePowerUp(String[] powerUpInfo){
        powerUpActivated = true;
        powerUp.setX(Integer.parseInt(powerUpInfo[0]));
        powerUp.setY(Integer.parseInt(powerUpInfo[1]));
    }

    public void activatePowerup(String type) {
        Thread delayedThread = new Thread(() -> {
            try {
                if (type.equals("SCOREMULTIPLIER")) {
                    scoreMultiplier = 2;
                    powerupLog = "2X score";
                    Thread.sleep(5000);
                    scoreMultiplier = 1;
                    powerupLog = "";
                } else if (type.equals("GRAVITY")) {
                    gravity = true;
                    powerupLog = "activatable gravity";
                    Thread.sleep(10000);
                    gravity = false;
                    powerupLog = "";
                } else if (type.equals("INVINCIBILITY")) {
                    invincibility = true;
                    powerupLog = "invincibility";
                    Thread.sleep(5000);
                    invincibility = false;
                    powerupLog = "";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        delayedThread.start();
    }

    public void updateOpponentStatus(int id, int opponentScore) {
        opponentLog += "Player " + id + " has lost.\nFinal score: " + opponentScore;
    }

    public void gameOver(int winnerID, int winnerScore) {
        System.out.println("cscreen: " + winnerID + " " + winnerScore);
        System.out.println("me: " + id + " " + score);
        if (id == winnerID) {
            winner = true;
            playSound("sfx-win");
        } else {
            this.winnerScore = winnerScore;
            playSound("sfx-lose");
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setId(int id) {
        this.id = id;
        System.out.println("ID was set to " + id);
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
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) && player.getY() >= 400-player.getHeight()) { // Must be on the ground to jump.
            jumpStrength = 24; // upwards velocity
            jumping = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (gravity) {
                jumpStrength = -24;
            }
        }

        else if(e.getKeyCode() == KeyEvent.VK_D) { // TESTING CHEAT KEY
            score += 500;
        } else if(e.getKeyCode() == KeyEvent.VK_F) {
            score -= 500;
        }
    }

    public void playSound(String name) {
        String fileName = name + ".wav";
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

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
