package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class ButtonHandler implements ActionListener {
    GUI gui;

    public ButtonHandler(GUI gui) {
        this.gui = gui;
    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println(evt.getActionCommand()); // string
    }
}

class ImagePanel extends JPanel {
    ImageIcon icon;

    public ImagePanel() {
        super();
        icon = new ImageIcon();
        JLabel label = new JLabel(icon);
        add(label, BorderLayout.CENTER);
        this.setSize(200, 200);
    }

    public void refresh(byte[] data) {
        Image theImage = getToolkit().createImage(data);
        getToolkit().prepareImage(theImage, -1, -1, null);
        icon.setImage(theImage);
        icon.paintIcon(this, this.getGraphics(), 5, 5);
    }
}

public class GUI {
    private Monitor monitor;
    private ImagePanel imagePanel1;
    private ImagePanel imagePanel2;

    public GUI(Monitor monitor) {
        this.monitor = monitor;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                show();
            }
        });
    }

    public void show() {
        JFrame frame = new JFrame("Camera Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        frame.setLayout(new GridLayout(0, 2));
        frame.setResizable(false);

        // Labels
        JLabel label1 = new JLabel("Camera 1", SwingConstants.CENTER);
        JLabel label2 = new JLabel("Camera 2", SwingConstants.CENTER);
        frame.add(label1);
        frame.add(label2);

        imagePanel1 = new ImagePanel();
        imagePanel2 = new ImagePanel();
        frame.add(imagePanel1);
        frame.add(imagePanel2);

        // Buttons
        JButton idleButton = new JButton("Idle Mode");
        JButton movieButton = new JButton("Movie Mode");
        idleButton.addActionListener(new ButtonHandler(this));
        idleButton.setFont(new Font("Arial", Font.BOLD, 40));
        movieButton.addActionListener(new ButtonHandler(this));
        movieButton.setFont(new Font("Arial", Font.BOLD, 40));
        frame.add(idleButton);
        frame.add(movieButton);

        frame.pack();

        // Center GUI
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        frame.setVisible(true);
    }
}
