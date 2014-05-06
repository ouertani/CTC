package org.technozor.jzon;

import java.util.function.Function;

/**
 * Created by slim on 4/24/14.
 */
@FunctionalInterface
public interface Writer<T> extends Function<T,String> { }
