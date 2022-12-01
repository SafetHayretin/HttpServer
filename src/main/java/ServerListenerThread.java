import org.apache.commons.cli.CommandLine;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerListenerThread extends Thread {

    private final CommandLine cmd = Main.cmd;
    private final int port;

    private final String webRoot;

    public ServerListenerThread(int port, String webRoot) {
        this.port = port;
        this.webRoot = webRoot;
    }

    @Override
    public void run() {
        int threads = 1;
        if (cmd.hasOption("t")) {
            threads = Integer.parseInt(cmd.getOptionValue("t"));
        }
        try (ServerSocket serverSocket = new ServerSocket(port))  {
            ExecutorService pool = Executors.newFixedThreadPool(threads);

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                HttpThread httpThread = new HttpThread(socket, webRoot);
                pool.submit(httpThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
