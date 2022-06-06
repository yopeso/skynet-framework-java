package skynet.utils;

public class Commands {
    public static final String GET_PROCESSES = Utils.isWindows() ? System.getenv("windir") + "\\system32\\" + "tasklist.exe /fo csv /nh" : "ps -ax";
}
