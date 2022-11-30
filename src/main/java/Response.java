import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Response {
    private final String CRLF = "\n\r";

    String webRoot;

    String contentType;

    String file;

    String path;

    String lastModified;

    String contentEncoding;


    public Response(String path, String webRoot, String contentEncoding) {
        this.path = path;
        this.webRoot = webRoot;
        this.file = getHtml();
        this.contentEncoding = contentEncoding;
        setContentType();
    }

    public String createResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK" + CRLF);
        sb.append("Content-Length: " + file.getBytes().length + CRLF);
        sb.append("Date: " + new Date() + CRLF);
        sb.append("Last-Modified: " + lastModified + CRLF);
//        if (contentEncoding != null) {
//            sb.append("Content-Encoding: " + contentEncoding + CRLF);
//        }
        if (contentType != null) {
            sb.append("Content-Type: " + contentType + CRLF);
        }
        sb.append(CRLF + file + CRLF + CRLF);

        return sb.toString();
    }

    private String getHtml() {
        File file = new File(webRoot + path);
        lastModified = setLastModified(file);
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private String setLastModified(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    private void setContentType() {
        if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            contentType = "image/jpeg";
        else if (path.endsWith(".html") || path.endsWith(".htm"))
            contentType = "text/html";
        else if (path.endsWith(".mp3"))
            contentType = "audio/mpeg";
        else if (path.endsWith(".pdf"))
            contentType = "application/pdf";
        else if (path.endsWith(".png"))
            contentType = "image/png";
        else if (path.endsWith(".css"))
            contentType = "text/css";
    }
}
