package org.technozor.jzon;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * Created by slim on 4/24/14.
 */
@Target({TYPE, TYPE_USE})
@Retention(RetentionPolicy.CLASS)
public @interface Jzon {
}
