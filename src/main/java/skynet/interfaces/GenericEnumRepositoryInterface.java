package skynet.interfaces;

import java.util.ArrayList;

public interface GenericEnumRepositoryInterface {
    String by();

    String selector();

    ArrayList<String> valuesOf();
}
