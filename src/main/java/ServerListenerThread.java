import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerListenerThread extends Thread {

    private int port;

    private String webRoot;

    ServerSocket serverSocket;

    public ServerListenerThread(int port, String webRoot) throws IOException {
        this.port = port;
        this.webRoot = webRoot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(5);

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                HttpThread httpThread = new HttpThread(socket, webRoot);
                pool.submit(httpThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
