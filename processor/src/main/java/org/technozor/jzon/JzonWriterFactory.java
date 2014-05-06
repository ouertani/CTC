package org.technozor.jzon;


import org.technozor.jzon.internal.processor.JzonConstants;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

/**
 * Created by slim on 4/24/14.
 */
public class JzonWriterFactory {


    public static <T> Writer<T> writer(Class<T> clazz) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            String className = clazz.getCanonicalName() + JzonConstants.MAPPER_CLASS_SUFFIX;
            Class<Writer<@Jzon T>> mapperImpl = (Class<Writer<@Jzon T>>) classLoader.loadClass(className);
            MethodHandle constructor = MethodHandles.lookup().findConstructor(mapperImpl, methodType(Void.class));
            return (Writer<T>) constructor.invoke();
        } catch (Throwable throwable) {
            return toRuntimeException(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T toRuntimeException(Throwable throwable) throws T {
        throw (T) throwable;
    }

}
