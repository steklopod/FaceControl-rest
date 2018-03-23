package ru.steklopod.tv.later;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class LambdaTextFilter {

    private static Logger logger = LoggerFactory.getLogger(LambdaTextFilter.class);


    @Test
    @DisplayName("Склейка текста без лямбд")
    public void testExternal() {
        StringBuilder sb = new StringBuilder();

        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");
        for (String s : strings) {
            sb.append(s);
        }
        assertEquals(
                "FooBarBaz",
                sb.toString()
        );
    }

    @Test
    @DisplayName("Склейка текста с лямбдами")
    public void testInternal() {
        StringBuilder sb = new StringBuilder();

        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");
        strings.forEach(sb::append);

        assertEquals(
                "FooBarBaz",
                sb.toString()
        );
        System.err.println(strings);
        System.err.println(strings.get(1));
    }

    //////////////////////////////////////////////
    private int invocations = 0;

    @Test
    public void testLazy() {
        Stream<String> stream = Arrays.asList("Foo", "Marco", "Bar", "Polo", "Baz")
                .stream()
                .filter((s) -> {
                    invocations++;
                    return s.length() == 3;
                });

        Iterator<String> i = stream.iterator();

        assertEquals("Foo", i.next());
        assertEquals(1, invocations);

        assertEquals("Bar", i.next());
        assertEquals(3, invocations);

        assertEquals("Baz", i.next());
        assertEquals(5, invocations);
    }

    @Test
    void testEager() {
        List<String> list = Arrays.asList("Foo", "Marco", "Bar", "Polo", "Baz")
                .stream()
                .filter((s) -> {
                    invocations++;
                    return s.length() == 3;
                })
                .collect(Collectors.toList());

        assertEquals(3, list.size());
        assertEquals(5, invocations);

        System.err.println(list);
    }

    @Test
    void testEager2() {
        List<String> list = Arrays.asList("S", "SS", "SSS", "SSSS");
        list.stream()
                .filter((s) -> s.length() > 2).collect(Collectors.toList());
        System.err.println(list);
    }

    @Test
    void skipLimit() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Кот");
        stringList.add("Пёс");
        stringList.add("Котопёс");
        stringList.add("Котяра");
//        stringList.add(null);

        List<String> resultList = stringList.stream()
                .filter(value -> value.startsWith("Кот"))
                .skip(1)
                .limit(2)
                .collect(Collectors.toList());
        resultList.forEach(value -> System.out.println(value));
    }

}
