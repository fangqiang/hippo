package pers.fq.hippo.common.tag;

import java.lang.annotation.*;

/**
 * 说明数据已经排序
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Sorted {
}