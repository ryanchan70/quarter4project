import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientScreen extends JPanel implements ActionListener {
    private JButton startButton;

    public ClientScreen() {
        startButton = new JButton("Start");
        startButton.setBounds(350,500,100,30);
        add(startButton);
        startButton.addActionListener(this);

        setLayout(null);
        setFocusable(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(38, 41, 45));
        g.fillRect(0, 0, 800,600);
        g.setColor(Color.white);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton){
            startButton.setVisible(false);
        }
    }
}
