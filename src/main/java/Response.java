import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Response {
    private final String CRLF = "\n\r";

    String webRoot;

    String contentType;

    File file;

    String path;

    String lastModified;

    String contentEncoding;


    public Response(String path, String webRoot, String contentEncoding) {
        this.path = path;
        this.webRoot = webRoot;
        this.file = getFile();
        this.contentEncoding = contentEncoding;
        setContentType();
    }

    public String createResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK" + CRLF);
        sb.append("content-length: " + file.length() + CRLF);
        sb.append("date: " + new Date() + CRLF);
        sb.append("last-modified: " + lastModified + CRLF);
//        if (contentEncoding != null) {
//            sb.append("content-encoding: " + contentEncoding + CRLF);
//        }
        if (contentType != null) {
            sb.append("content-type: " + contentType + CRLF + CRLF);
        }

        return sb.toString();
    }

    private File getFile() {
        File file = new File(webRoot + path);
        System.out.println(file.getAbsolutePath());
        lastModified = setLastModified(file);

        return file;
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
