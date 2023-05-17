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
    private boolean ready;
    private boolean obstacleSent;
    private boolean startGame;
    Obstacle obstacle = new Obstacle(500,500,50,50);

    public ClientScreen() {
        startGame = false;
        startButton = new JButton("Start");
        startButton.setBounds(350, 500, 100, 30);
        add(startButton);
        startButton.addActionListener(this);

        ready = false;

        setLayout(null);
        setFocusable(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
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
        }
        else {
            g.setColor(Color.BLUE);
            g.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getHeight(), obstacle.getLength());

        }
        System.out.println("clientscreen: " + numReady + " " + numClients);
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
            startButton.setVisible(false);
        }
        repaint();
    }

    public void obstacleSent(){
        this.obstacleSent = true;
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
}
