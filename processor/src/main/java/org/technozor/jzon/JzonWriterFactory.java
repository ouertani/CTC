package org.technozor.jzon;


import org.technozor.jzon.internal.processor.JzonConstants;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import static java.lang.invoke.MethodType.*;

/**
 * Created by slim on 4/24/14.
 */
public class JzonWriterFactory {


    public static <T> Writer<T> writer(Class<T> t) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            String s = t.getCanonicalName() + JzonConstants.MAPPER_CLASS_SUFFIX;
            Class<Writer<@Jzon T>> mapperImpl  = (Class<Writer<@Jzon T>>) classLoader.loadClass(s);
            MethodHandle constructor = lookup.findConstructor(mapperImpl, methodType(Void.class));
            return (Writer<T>) constructor.invoke();
        }  catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

}
