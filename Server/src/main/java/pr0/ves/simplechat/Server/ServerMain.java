package pr0.ves.simplechat.Server;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ServerMain implements MessageListener {
    private final JFrame frame = new JFrame();
    private final ServerPanel panel = new ServerPanel();
    private final ArrayList<ServerThread> clientsThreads = new ArrayList<>();
    private final ServerWeb web = new ServerWeb();
    private ServerSocket serverSocket;
    private boolean up = false;

    public static void main(String[] args) {
        ServerMain main = new ServerMain();
    }

    private ServerMain() {
        panel.getStartButton().addActionListener(actionEvent -> {
            if (!up) {
                up = true;
                try {
                    startServer();
                    if (!serverSocket.isClosed()) {
                        panel.getStartButton().setText("Stop");
                        panel.getServerLabel().setText("Server Up: " + serverSocket.getInetAddress().getHostAddress());
                        refreshClientPanel();
                        frame.pack();
                    }
                    new Thread(() -> runWhileUp(this::checkClientList, 5)).start();
                    new Thread(() -> runWhileUp(this::listenToClients)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                up = false;
                panel.getStartButton().setText("Start Server");
                panel.getServerLabel().setText("Server Down");
                clientsThreads.clear();
                checkClientList();
                frame.pack();
            }
        });

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        frame.setContentPane(panel.getServerPanel());
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int confirm = JOptionPane.showOptionDialog(frame,
                        "Are You Sure?",
                        "Exit", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(1);
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
        frame.setTitle("Server");
        frame.pack();
        frame.setVisible(true);
    }

    private void listenToClients() {
        try {
            ServerThread st = new ServerThread(serverSocket.accept());
            st.addListener(this);
            st.addListener(this.web);
            clientsThreads.add(st);
            st.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkClientList() {
        clientsThreads.removeIf(s -> !s.isConnectionCheck());
        clientsThreads.forEach(s -> {
            if (s.isInitialized()) {
                s.getClientData().out.println("KEEPALIVE");
                System.out.println("sent keepalive to " + s.getClientData().getName());
                s.setConnectionCheck(false);
            }
        });
        refreshClientPanel();
    }

    private void runWhileUp(Runnable runnable) {
        while (up) {
            runnable.run();
        }
    }


    private void runWhileUp(Runnable runnable, int secondsToWait) {
        while (up) {
            runnable.run();
            try {
                TimeUnit.SECONDS.sleep(secondsToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void startServer() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(8189);
        }
    }

    private void refreshClientPanel() {
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> addressList = new ArrayList<>();
        clientsThreads.stream().filter(s -> s.getClientData() != null).forEach(s -> {
            nameList.add(s.getClientData().getName());
            addressList.add(s.getClientData().getAddress().getHostAddress());
        });

        //noinspection unchecked
        panel.getNameList().setListData(nameList.toArray());
        //noinspection unchecked
        panel.getAddressList().setListData(addressList.toArray());

        frame.pack();

    }

    private boolean checkClientName(ServerThread serverThread) {
        boolean err = false;
        for (ServerThread s : clientsThreads) {
            if ((s.getClientData().getName().equals(serverThread.getClientData().getName()) && !s.getClientData().getAddress().equals(serverThread.getClientData().getAddress()))) {
                err = true;
            }
        }
        return err;
    }

    @Override
    synchronized public void processMessage(ServerThread serverThread, String message) {
        if (message.startsWith("CHECKNAME")) {
            serverThread.setName(message.split("=", 2)[1].trim());
            boolean alreadyUsed = checkClientName(serverThread);
            if (alreadyUsed) {
                serverThread.getClientData().out.println("CHECKNAME=ERROR");
            } else {
                serverThread.setInitialized(true);
                serverThread.getClientData().out.println("CHECKNAME=OK");
                clientsThreads.forEach(
                        s -> s.getClientData().out.println("MESSAGE=" + serverThread.getClientData().getName() + " is connecting."));
            }
        } else if (message.startsWith("MESSAGE") && message.split("=", 2)[1] != null) {
            clientsThreads.forEach(s ->
                    s.getClientData().out.println("MESSAGE=" + serverThread.getClientData().getName() + ": " + message.split("=", 2)[1]));
        } else if (message.startsWith("EXIT")) {
            clientsThreads.forEach(
                    s -> s.getClientData().out.println("MESSAGE=" + serverThread.getClientData().getName() + " left"));
            clientsThreads.remove(serverThread);
            serverThread.getClientData().out.println("EXIT=SUCCESS");
            serverThread.interrupt();
        } else if (message.startsWith("KEEPALIVE")) {
            serverThread.setConnectionCheck(true);
        }
    }


}

