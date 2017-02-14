package pr0.ves.simplechat.Server;

import javax.swing.*;

class ServerPanel {
    private JButton startButton;
    private JPanel serverPanel;
    private JLabel serverLabel;
    private JPanel listPanel;
    private JList nameList;
    private JList addressList;

    public JList getNameList() {
        return nameList;
    }

    public JList getAddressList() {
        return addressList;
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    public JLabel getServerLabel() {
        return serverLabel;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JPanel getServerPanel() {
        return serverPanel;
    }

}
