package org.technozor.jzon;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;

import static java.lang.annotation.ElementType.*;

/**
 * Created by slim on 4/24/14.
 */
@Target({TYPE, TYPE_USE})
@Retention(RetentionPolicy.CLASS)
public @interface Jzon { }
