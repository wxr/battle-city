package edu.uchicago.mpcs51036.mvc.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.AWTEvent;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private JPanel contentPane;
    private BorderLayout borderLayout = new BorderLayout();

    public GameFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout);
    }

    @Override
    // Override so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

}


