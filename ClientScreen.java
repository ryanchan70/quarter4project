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

    public ClientScreen() {
        startButton = new JButton("Start");
        startButton.setBounds(350, 500, 100, 30);
        add(startButton);
        startButton.addActionListener(this);

        ready = false;
        startGame = false;

        setLayout(null);
        setFocusable(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!startGame) {
            g.setColor(new Color(38, 41, 45));
            g.fillRect(0, 0, 800, 600);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.BOLD, 40));
            g.drawString("Welcome!", 300, 85);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.drawString("Please wait for all players to press start.", 170, 175);
            g.drawString(numReady + "/" + numClients + " players ready", 300, 250);
            System.out.println("clientscreen: " + numReady + " " + numClients);
        } else {
            //show player sprite and obstacles
        }
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
        repaint();
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
