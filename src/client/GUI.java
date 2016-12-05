package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
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
    int mode;
    Monitor monitor;

    public ButtonHandler(GUI gui, int mode, Monitor monitor) {
        this.gui = gui;
        this.mode = mode;
        this.monitor = monitor;
    }

    public void actionPerformed(ActionEvent evt) {
        //System.out.println(evt.getActionCommand()); // string
        monitor.forceMode(mode);
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
        Dimension size = getSize();
        theImage = theImage.getScaledInstance((int)size.getWidth(), (int)size.getHeight(), java.awt.Image.SCALE_SMOOTH);
        getToolkit().prepareImage(theImage, -1, -1, null);
        icon.setImage(theImage);
        icon.paintIcon(this, this.getGraphics(), 5, 5);
    }
}

public class GUI {
    private Monitor monitor;
    private ImagePanel imagePanel1;
    private ImagePanel imagePanel2;
    private JLabel statusLabel;

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
        JButton autoButton = new JButton("Auto Mode");
        idleButton.addActionListener(new ButtonHandler(this, Monitor.MODE_IDLE, monitor));
        idleButton.setFont(new Font("Arial", Font.BOLD, 40));
        movieButton.addActionListener(new ButtonHandler(this, Monitor.MODE_MOVIE, monitor));
        movieButton.setFont(new Font("Arial", Font.BOLD, 40));
        autoButton.addActionListener(new ButtonHandler(this, Monitor.MODE_AUTO, monitor));
        autoButton.setFont(new Font("Arial", Font.BOLD, 40));
        frame.add(idleButton);
        frame.add(movieButton);
        frame.add(autoButton);

        statusLabel = new JLabel("Hello?", SwingConstants.CENTER);
        frame.add(statusLabel);

        frame.pack();

        // Center GUI
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        frame.setVisible(true);
    }

    int n = 0;
    long lastT = System.currentTimeMillis();

    public void put(common.Image image, boolean synchronous) { //show mode?
        class ImagePutter implements Runnable {
            private common.Image image;
            private boolean synchronous;

            public ImagePutter(common.Image image, boolean synchronous) {
                this.image = image;
                this.synchronous = synchronous;
            }

            public void run() {
                String s = "<html>";

                // Display mode.
                s += "Mode: ";
                int forcedMode = monitor.getForcedMode();
                int mode = monitor.getMode();
                if (forcedMode == Monitor.MODE_AUTO) {
                    s += "auto (" + (mode == Monitor.MODE_IDLE ? "idle" : "movie") + ")";
                } else {
                    s += mode == Monitor.MODE_IDLE ? "idle" : "movie";
                }
                s += "<br>";

                // Display synchronized/asynchronized.
                s += (synchronous ? "synchronous" : "asynchronous") + "<br>";

                // Display FPS (not updated).
                /*long t = System.currentTimeMillis();
                double fps = 1000.0 / (t - lastT);
                lastT = t;
                s += "Frame " + n++ + " (FPS = " + (int)fps + ")<br>";*/

                int id = image.getCameraId();
                ImagePanel panel = null;
                switch (id) {
                    case 0: panel = imagePanel1; break;
                    case 1: panel = imagePanel2; break;
                }

                s += "</html>";
                panel.refresh(image.getData());
                statusLabel.setText(s);
            }
        }
        javax.swing.SwingUtilities.invokeLater(new ImagePutter(image, synchronous));
    }
}
