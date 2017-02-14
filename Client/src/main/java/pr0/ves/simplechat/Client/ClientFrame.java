package pr0.ves.simplechat.Client;

import javax.swing.*;
import java.awt.*;

class ClientFrame {
    private JTextArea textArea1;
    private JFormattedTextField messageField;
    private JButton sendButton;
    private JPanel clientPanel;
    private boolean pressed = false;
    private JPanel textPanel;

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public JFormattedTextField getMessageField() {
        return messageField;
    }

    public JPanel getClientPanel() {
        return clientPanel;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

}
