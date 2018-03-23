package ru.steklopod.tv.later;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class StreamAPITest {

    @Test
    public void test1() {
        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");
        Stream<String> stream = strings.stream();
        assertEquals(
                "Foo",
                stream.findFirst().get()
        );
    }

    @Test
    public void test2() {
        List<String> list = Arrays.asList("Bar", "Baz");
        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");

        Stream<String> stream = strings.stream();

        Stream<String> b = stream.filter(s -> s.startsWith("B"));

        List<String> collect = b.collect(Collectors.toList());

        System.err.println(collect);
//                        .stream()
//                        .filter((s) -> s.startsWith("B"))
//                        .collect(Collectors.toList())
        assertEquals(list, collect);
    }

    @Test
    public void test3() {
        assertEquals(
                Arrays.asList(3, 3, 3),
                Arrays.asList("Foo", "Bar", "Baz")
                        .stream()
                        .map(String::length)
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void test4() {
        assertEquals(
                Integer.valueOf(9),
                Arrays.asList("Foo", "BarBar", "BazBazBaz")
                        .stream()
                        .map(String::length)
                        .reduce((l, r) -> (l > r ? l : r))
                        .get()
        );
    }

    @Test
    public void test5() {
        assertEquals(
                Arrays.asList("Foo", "Bar", "Baz"),
                Arrays.asList("Foo Bar Baz")
                        .stream()
                        .flatMap((element) -> Arrays.stream(element.split(" ")))
                        .collect(Collectors.toList())
        );
    }

    @SuppressWarnings("serial")
    @Test
    public void test6() {
        assertEquals(
                new ArrayList<String>() {{
                    add("Foo");
                    add("Bar");
                    add("Baz");
                }},
                Arrays.asList("Foo", "Bar", "Baz", "Baz", "Foo", "Bar")
                        .stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void test7() {
        assertEquals(
                Arrays.asList("Bar", "Baz", "Foo"),
                Arrays.asList("Foo", "Bar", "Baz")
                        .stream()
                        .sorted(String::compareTo)
                        .collect(Collectors.toList())
        );
    }


}
