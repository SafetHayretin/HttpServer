import Exceptions.IncorrectCommand;
import org.apache.commons.cli.*;

public class Main {
    public static CommandLine cmd;

    public static void main(String[] args) {
        if (!args[0].equals("http-server")) {
            throw new IncorrectCommand("http-server [path] [options]");
        }

        cmd = getCmd(args);
        if (cmd.hasOption("h")){
            System.out.println(cmd.getArgList());
            return;
        }
        int port = 8080;
        String webRoot = args[1];
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

    public static CommandLine getCmd(String[] args) {
        Options options = new Options();

        Option port = new Option("p", "port", true, "Port to use. Use -p 0 to look for an open port, starting at 8080. It will also read from process.env.PORT.");
        options.addOption(port);

        Option threads = new Option("t", "threads", true, "Number of threads");
        options.addOption(threads);

        Option directory = new Option("d", "Show directory listings");
        options.addOption(directory);

        Option compressFiles = new Option("c", "compress", false, "Compress text files");
        options.addOption(compressFiles);

        Option compress = new Option("g", "gzip", false, "Return compressedFiles");
        options.addOption(compress);

        Option help = new Option("h", "help", false, "Show commands");
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
