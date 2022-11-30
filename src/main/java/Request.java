import java.util.Date;
import java.util.Scanner;

public class Request {
    String fileName;

    String userAgent;

    String encoding;

    public Request(Scanner request) {
        handleRequest(request);
    }

    private void handleRequest(Scanner in) {
        Date date = new Date();
        while (true) {
            String line = in.nextLine();
            if (line.startsWith("GET")) {
                System.out.println("[" + date + "] " + line);
                fileName = getFileName(line);
            }
            if (line.startsWith("User-Agent")) {
                System.out.println("[" + date + "] " + line);
                userAgent = getUserAgent(line);
            }
            if (line.startsWith("Accept-Encoding")) {
                encoding = getEncoding(line);
            }
            if (line.isEmpty())
                break;
        }
    }

    private String getEncoding(String line) {
        String[] strings = line.split("Accept-Encoding: ");
        if (strings[1].contains("gzip"))
            return "gzip";
        return null;
    }

    private String getUserAgent(String line) {
        String[] strings = line.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < strings.length; i++) {
            sb.append(strings[i]);
        }

        return sb.toString();
    }

    private String getFileName(String line) {
        String[] strings = line.split(" ");
        String requestedFile = strings[1];
        requestedFile = requestedFile.replaceAll("20%", " ");

        if (requestedFile.length() == 1)
            return "index.html";

        return requestedFile;
    }
}
