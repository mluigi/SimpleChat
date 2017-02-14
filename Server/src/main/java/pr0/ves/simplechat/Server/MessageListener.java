package pr0.ves.simplechat.Server;

interface MessageListener {
    void processMessage(ServerThread serverThread, String message);
}
