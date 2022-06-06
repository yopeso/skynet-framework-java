package skynet.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    @SuppressWarnings(value = "all")
    private static String getScript(String filePath) throws IOException {
        InputStream input = new FileInputStream(filePath);

        Path path = Paths.get(filePath);

        if(!Files.isExecutable(path)) {
            Runtime.getRuntime().exec(new String[]{"bash", "-c", "chmod 700 " + path.toString()});
        }

        return filePath;
    }

    /**
     * Executes a command on the command line (cmd for windows, else bash)
     *
     * @param cmd command to run
     * @return result of command
     */
    public static String executeCMD(String cmd, String process, Action action) throws Exception {
        long ts = System.currentTimeMillis();
        String[] cmds;
        Process p = null;
        if (!isWindows()) {
            cmds = new String[]{cmd.replaceAll("\"", "\\\\\"")};
        }
        else {
            cmds = new String[]{"cmd.exe", "/c", cmd};
        }
        Logger.info("Executing command : " + cmd);

        try {
            if (isWindows()) {
                ProcessBuilder builder = new ProcessBuilder(cmds);
                builder.redirectErrorStream(true);
                p = builder.start();
            }
            else {
                if(isValidPath(cmd)) {
                    p = Runtime.getRuntime().exec(getScript(cmd));
                } else {
                    cmds = new String[]{"bash", "-c", cmd};
                    p = Runtime.getRuntime().exec(cmds);
                }
            }
        }
        catch (Throwable e1) {
            Logger.warn("issue to execute command : " + e1.getMessage());
        }
        finally {
            if (p != null) {

                int timeout = 60;
                while (p != null && timeout > 0) {
                    // getRuntime: Returns the runtime object associated with the current Java application.
                    // exec: Executes the specified string command in a separate process.
                    Process processes = Runtime.getRuntime().exec(Commands.GET_PROCESSES);
                    BufferedReader input = new BufferedReader(new InputStreamReader(processes.getInputStream()));

                    if (action.equals(Action.START)) {
                        if (input.lines().collect(Collectors.toList()).stream().anyMatch(s -> s.contains(process))) {
                            Logger.info(process + " started");
                            p.destroy();
                            p = null;
                        }
                    }
                    else {
                        if (input.lines().collect(Collectors.toList()).stream().anyMatch(s -> !s.contains(process))) {
                            Logger.info(process + " killed");
                            p.destroy();
                            p = null;
                            break;
                        }
                    }

                    input.close();
                    timeout--;
                }
            }
            Logger.info("--> EXECUTED IN: " + (System.currentTimeMillis() - ts) + " : " + Arrays.toString(cmds));
        }

        if (p != null) {
            if (action.equals(Action.START)) {
                throw new Exception("Couldn't find process: " + process);
            }

            if (action.equals(Action.KILL)) {
                throw new Exception("Couldn't find process: " + process);
            }
        }

        return null;
    }

    /**
     * Checks if the machine is running windows
     *
     * @return true if running on a Windows machine
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Attempts to convert an object into an int
     *
     * @param number object to convert
     * @param ret    value to return if conversion fails
     * @return result of conversion
     */
    public static int parseInt(Object number, int ret) {
        try {
            if (number == null) {
                return ret;
            }
            if (number instanceof Float) {
                return ((Float) number).intValue();
            }
            if (number instanceof Double) {
                return ((Double) number).intValue();
            }
            return Integer.parseInt(number.toString().replaceAll(",", "").split("\\.")[0]);
        }
        catch (Exception ex) {
            return ret;
        }
    }

    /**
     * Converts json to "pretty" format
     *
     * @param o input json
     * @return formatted JSON as string
     */
    public static String jsonPretty(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(o);
    }

    /**
     * Sleeps for a given time
     *
     * @param sleepTime time to sleep in millis
     * @param msg       info message to display
     * @return true if sleep interrupted
     */
    public static boolean threadSleep(long sleepTime, String msg) {
        Thread cur = Thread.currentThread();
        try {
            if (msg != null) { Logger.debug("Thread \"" + cur.getName() + "\" sleeping for: " + sleepTime + "ms"); }
            Thread.sleep(sleepTime);
            if (msg != null) { Logger.debug("Thread \"" + cur.getName() + " is awake"); }
            return false;
        }
        catch (InterruptedException e) {
            if (msg != null) { Logger.debug("Thread \"" + cur.getName() + " was interrupted:" + e.getMessage()); }
            return true;
        }
    }

    /**
     * Checks if a string is a valid path.
     * Null safe.
     *
     * Calling examples:
     *    isValidPath("c:/test");      //returns true
     *    isValidPath("c:/te:t");      //returns false
     *    isValidPath("c:/te?t");      //returns false
     *    isValidPath("c/te*t");       //returns false
     *    isValidPath("good.txt");     //returns true
     *    isValidPath("not|good.txt"); //returns false
     *    isValidPath("not:good.txt"); //returns false
     */
    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, new ToStringStyleObject(false));
    }

    public enum Action {
        START,
        KILL
    }
}
