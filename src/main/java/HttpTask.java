import org.apache.commons.cli.CommandLine;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HttpThread implements Runnable {
    private final CommandLine cmd = Main.cmd;

    private final Socket socket;

    private final String webRoot;

    public HttpThread(Socket socket, String webRoot) {
        this.socket = socket;
        this.webRoot = webRoot;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            if (!isIndexHtmlExist()) {
                if (cmd.hasOption("d"))
                    listFilesInDirectory(outputStream);
                else {
                    System.out.println("Missing index.html file.");
                }
                return;
            }

            Scanner in = new Scanner(inputStream, StandardCharsets.UTF_8);
            Request request = new Request(in);

            String fileName = request.fileName;
            String encoding = request.encoding;
            Response response = new Response(fileName, webRoot, encoding);
            String responseStr = response.getHeader();
            outputStream.write(responseStr.getBytes());
            outputStream.flush();
            File file = response.file;
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            for (int n; -1 != (n = fileInputStream.read(buf)); ) {
                outputStream.write(buf, 0, n);
            }

            outputStream.flush();
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void listFilesInDirectory(OutputStream outputStream) throws IOException {
        File[] files = new File(webRoot).listFiles();
        StringBuilder body = new StringBuilder();
        assert files != null;
        for (File f : files) {
            body.append(f.toPath()).append("\n");
        }
        String filesInDir = body.toString();
        String sb = "HTTP/1.1 200 OK" + "\n\r" +
                "content-length: " + filesInDir.length() + "\n\r" + "\n\r" +
                filesInDir;

        System.out.println(filesInDir);
        outputStream.write(sb.getBytes());
    }

    private boolean isIndexHtmlExist() {
        File file = new File(webRoot + "index.html");
        return file.exists();
    }
}
