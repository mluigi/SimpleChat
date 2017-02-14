package pr0.ves.simplechat.common;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientData {
    private String name;
    private InetAddress address;
    public final PrintWriter out;
    public final BufferedReader in;

    public ClientData(String name, InetAddress address, BufferedReader in, PrintWriter out) {
        this.name = name;
        this.address = address;
        this.out = out;
        this.in = in;
    }

    public ClientData(String name, String address, BufferedReader in, PrintWriter out) {
        this.name = name;
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.in = in;
        this.out = out;
    }

    @Override
    public String toString() {
        return name+","+address.getHostAddress();
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
