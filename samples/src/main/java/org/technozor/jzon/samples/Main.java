package org.technozor.jzon.samples;

import org.technozor.jzon.JzonWriterFactory;
import org.technozor.jzon.Writer;

/**
 * Created by slim on 4/25/14.
 */
public class Main {

    static Writer<Person> writer = JzonWriterFactory.writer( Person.class);
    static Writer<Car> carWriter = JzonWriterFactory.writer ( Car.class);
    public static void main(String[] args) {



        Person pa = new Person();
        String apply = writer.apply(pa);
        System.out.println("----------"+apply);


        String apply1 = carWriter.apply(new Car());
        System.out.println(apply1);
    }

}
