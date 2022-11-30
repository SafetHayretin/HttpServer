import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public class HttpThread extends Thread {
    private final Socket socket;

    private final String webRoot;

    public HttpThread(Socket socket, String webRoot) {
        this.socket = socket;
        this.webRoot = webRoot;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            Scanner in = new Scanner(inputStream, StandardCharsets.UTF_8);
            Request request = new Request(in);

            outputStream = socket.getOutputStream();
            String fileName = request.fileName;
            String encoding = request.encoding;
            Response response = new Response(fileName, webRoot, encoding);
            String responseStr = response.createResponse();
            outputStream.write(responseStr.getBytes());
            sendFile(outputStream, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();

                if (socket != null)
                    socket.close();
            } catch (IOException ignored) {
            }
        }
    }
    private void sendFile(OutputStream outputStream, Response response) {
        try (InputStream inputStream = new FileInputStream(response.file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            for (int n; -1 != (n = inputStream.read(buf)); ) {
                out.write(buf, 0, n);
            }
            byte[] responseBytes = out.toByteArray();
            outputStream.write(responseBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
