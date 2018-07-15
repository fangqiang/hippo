package pers.fq.hippo.store.tag;

import java.lang.annotation.*;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/18
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface ThreadSafe {
}