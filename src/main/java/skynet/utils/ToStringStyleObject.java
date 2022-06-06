package skynet.utils;

import org.apache.commons.lang3.builder.ToStringStyle;

public class ToStringStyleObject extends ToStringStyle {
    public ToStringStyleObject (boolean useClassName) {
        super();
        this.setUseClassName(useClassName);
        this.setUseIdentityHashCode(false);
        this.setUseFieldNames(true);
    }
}
