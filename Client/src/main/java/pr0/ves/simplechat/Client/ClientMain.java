package pr0.ves.simplechat.Client;

import pr0.ves.simplechat.common.ClientData;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

class ClientMain {
    private final JFrame frame = new JFrame();
    private final ClientFrame panel = new ClientFrame();
    private ClientData clientData;
    private boolean check = false;

    private ClientMain() {

        try {
            String address = JOptionPane.showInputDialog("Insert server address");
            Socket socket = new Socket(InetAddress.getByName(address), 8189);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientData = new ClientData(JOptionPane.showInputDialog("Insert name."),
                    InetAddress.getLocalHost(), in, out);
            clientData.out.println("CHECKNAME=" + clientData.getName());
            checkClientName();
        } catch (IOException e) {
            System.exit(1);
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        SwingUtilities.updateComponentTreeUI(frame);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(4);
                        System.exit(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                clientData.out.println("EXIT");
                String input;
                try {

                    while (!(input = clientData.in.readLine()).equals("EXIT=SUCCESS")) {

                    }
                    System.exit(1);
                } catch (IOException e) {
                    System.exit(1);
                    e.printStackTrace();
                }
            }


            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        frame.setContentPane(panel.getClientPanel());
        panel.getMessageField().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!panel.isPressed()) {
                        panel.setPressed(true);
                        for (ActionListener a : panel.getSendButton().getActionListeners()) {
                            a.actionPerformed(new ActionEvent(panel.getSendButton(), 0, "enter pressed"));
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (panel.isPressed()) {
                        panel.setPressed(false);
                    }
                }
            }
        });
        panel.getSendButton().addActionListener(actionEvent -> {
            if (!panel.getMessageField().getText().equals("")) {
                clientData.out.println("MESSAGE" + "=" + panel.getMessageField().getText());
                panel.getMessageField().setText("");
            }
        });
        frame.pack();
        frame.setVisible(true);
        new Thread(this::listenToMessages).start();

    }

    public static void main(String[] args) {
        new ClientMain();
    }


    private void listenToMessages() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                String inputLine = this.clientData.in.readLine();
                if (inputLine.startsWith("MESSAGE=")) {
                    panel.getTextArea1().append(inputLine.split("=", 2)[1] + "\n");
                }else if (inputLine.equals("KEEPALIVE")){
                    clientData.out.println("KEEPALIVE");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkClientName() {
        while (!check) {
            try {
                String inputLine = clientData.in.readLine();
                //System.out.println(inputLine);
                if (inputLine.equals("CHECKNAME=ERROR")) {
                    clientData.setName(JOptionPane.showInputDialog("Name already exists.\nChoose a new name."));
                    clientData.out.println("CHECKNAME=" + clientData.getName());
                    checkClientName();
                } else if (inputLine.equals("CHECKNAME=OK")) {
                    check = true;
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
