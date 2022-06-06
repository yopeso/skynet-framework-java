package skynet.utils;

public abstract class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void info(String message){
        System.out.println(ANSI_WHITE + " : " + message + ANSI_RESET);
    }

    public static void success(String message){
        System.out.println(ANSI_GREEN + " : " + message + ANSI_RESET);
    }

    public static void warn(String message){
        System.out.println(ANSI_YELLOW + " : " + message + ANSI_RESET);
    }

    public static void error(String message){
        System.out.println(ANSI_RED + " : " + message + ANSI_RESET);
    }

    public static void exception(String message) throws Exception {message = ANSI_RED + " : " + message + ANSI_RESET; throw new Exception(message);}

    public static void debug(String message){
        System.out.println(ANSI_BLUE + " : " + message + ANSI_RESET);
    }
}