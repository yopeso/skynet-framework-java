package skynet;

import skynet.utils.Logger;

public abstract class Statics {
    private static String repoName = null;
    private static String uri = null;

    /**
     * DO NOT DELETE - A parameterless constructor is required!
     * Initializes Skynet AppSettings class.
     * Reads specific key values from the appSettings section in the appConfig.properties file for the Skynet assembly.
     *
     * @throws Exception - throws exception
     */
    public Statics () throws Exception {
    }

    public static void setURi (String url) {
        uri = url;
    }

    public static String getUri () {
        return uri;
    }

    public static String getRepoName () {
        return repoName;
    }

    public static void setRepoName (String repoName) {
        Logger.info(String.format("Update: active repository is '%s'", repoName));
        Statics.repoName = repoName;
    }
}
