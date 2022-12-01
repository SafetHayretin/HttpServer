import org.apache.commons.cli.CommandLine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class Response {
    CommandLine cmd = Main.cmd;

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

    public String getHeader() {
        StringBuilder sb = new StringBuilder();

        sb.append("HTTP/1.1 200 OK").append("\n\r");
        sb.append("content-length: ").append(file.length()).append("\n\r");
        sb.append("date: ").append(new Date()).append("\n\r");
        sb.append("last-modified: ").append(lastModified).append("\n\r");

        if (!isCompressedFileExist() && cmd.hasOption("c") && contentType.startsWith("text/"))
            compressFile();

        if (contentEncoding != null && cmd.hasOption("g") && isCompressedFileExist()) {
            sb.append("content-encoding: ").append(contentEncoding).append("\n\r");
            String filename = file.getAbsolutePath();
            file = new File(filename + ".gz");
            System.out.println(file.getAbsolutePath());
        }
        if (contentType != null) {
            sb.append("content-type: ").append(contentType).append("\n\r").append("\n\r");
        }

        return sb.toString();
    }

    private File getFile() {
        File file = new File(webRoot + path);
        lastModified = setLastModified(file);

        return file;
    }

    private String setLastModified(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    public byte[] getResponse() {
        byte[] responseBytes = null;
        try (InputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            for (int n; -1 != (n = inputStream.read(buf)); ) {
                out.write(buf, 0, n);
                responseBytes = out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBytes;
    }

    private boolean isCompressedFileExist() {
        String filename = file.getAbsolutePath();
        File compressedFile = new File(filename + ".gz");
        return compressedFile.exists();
    }

    private void compressFile() {
        String gzipFile = file.getAbsolutePath() + ".gz";
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(gzipFile);
             GZIPOutputStream gzipOS = new GZIPOutputStream(fos)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
