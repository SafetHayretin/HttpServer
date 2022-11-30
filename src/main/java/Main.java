import org.apache.commons.cli.*;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        CommandLine cmd = getCmd(args);

        int port = 8080;
        String webRoot = "C:\\Users\\Safet\\Desktop\\MyFirstWebPage\\";
        if (cmd.hasOption("p")) {
            String strPort = cmd.getOptionValue("p");
            port = Integer.parseInt(strPort);
        }
        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(port, webRoot);
            serverListenerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean checkExists(String directory) {
        File dir = new File(directory);
        File[] dir_contents = dir.listFiles();
        String temp = "index.html";
        boolean check = new File(directory, temp).exists();
        System.out.println("Check" + check);

        assert dir_contents != null;
        for (File dirContent : dir_contents) {
            if (dirContent.getName().equals(temp))
                return true;
        }

        return false;
    }

    public static String getHtml(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static CommandLine getCmd(String[] args) {
        Options options = new Options();

        Option port = new Option("p", true, "Port to use. Use -p 0 to look for an open port, starting at 8080. It will also read from process.env.PORT.");
        options.addOption(port);

        Option threads = new Option("t", true, "Number of threads");
        options.addOption(threads);

        Option directory = new Option("t", "Show directory listings");
        options.addOption(directory);

        Option help = new Option("h", "Show commands");
        options.addOption(help);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }
}
