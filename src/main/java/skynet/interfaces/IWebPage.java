package skynet.interfaces;

public interface IWebPage {
    /**
     * Waits for the web page to load.
     * The implementation must call one of the base class WaitForLoad method
     * overloads, either directly or by calling another WaitForLoad method overload
     * in the derived class.
     *
     * @param pageLoadTimeout - seconds to wait for the page to load
     */
    
    void waitForLoad(int pageLoadTimeout) throws Exception;
}
