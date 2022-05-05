import javax.swing.*;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class Input {


    public static JTextField textField1;

    public static void main(String[] args) {
        //declare major variable

        String ip1= JOptionPane.showInputDialog(new JFrame(), "Enter the IP address of Port scanner ");
        if(isValidInet4Address(ip1)){
            final ExecutorService es = Executors.newCachedThreadPool();
            final int timeout = 200;
            final List<Future<ScanResult>> futures = new ArrayList<>();
            ArrayList<Integer> arrport = new ArrayList<>();
            //try to connect 65535 ports
            for (int port = 1; port <= 65535; port++) {

                futures.add(portIsOpen(es, ip1, port, timeout));
            }
            try {
                //wait 200 milliseconds
                es.awaitTermination(200L, TimeUnit.MILLISECONDS);
                JOptionPane.showMessageDialog(new JFrame(), "Scanning...");
                //something went wrong
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(new JFrame(), "unable to listen port!");
            }
            int openPorts = 0;
            //try to display the result
            for (final Future<ScanResult> f : futures) {
                try {
                    if (f.get().isOpen()) {
                        openPorts++;
                        arrport.add(f.get().port());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(new JFrame(), "unable to listen port");
                }
            }
            //set text to empty
            JOptionPane.showMessageDialog(new JFrame(),"The Open port list:" + arrport + "\nThere are " + openPorts + " open ports on host "
                    + ip1 + " (probed with a timeout of "
                    + timeout + "ms)");
            es.shutdown();

        }else{
            JOptionPane.showMessageDialog(new JFrame(), "Please enter valid Ip address!");
        }

        }
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     *     Method      isValidInet4Address(String ip)
     *     Description  Is this ip address really valid?
     *     @author      <i>Jason</i>
     * </pre>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    public static boolean isValidInet4Address(String ip) {
        try {
            return Inet4Address.getByName(ip)
                    .getHostAddress().equals(ip);
        } catch (UnknownHostException ex) {
            return false;
        }
    }
    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * <pre>
     *     Method      portIsOpen(final ExecutorService es, final String ip, final int port,
     *                                                     final int timeout)
     *     Description  The connection part of the code, set the ip address, how many port we want, and how long before
     *     we deemed non-exist.
     *     @author      <i>Jason</i>
     * </pre>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
        public static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port,
                                                    final int timeout) {
            return es.submit(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return new ScanResult(port, true);
                } catch (IOException ex) {
                    return new ScanResult(port, false);
                }
            });
        }

    private void createUIComponents() {
        textField1.setText("");
    }

    public record ScanResult(int port, boolean isOpen) {

    }
}

