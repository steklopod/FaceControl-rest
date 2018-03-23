package ru.steklopod.tv.later;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@DisplayName("Паралель - Послед.")
public class SeqParTest {

    private static final Integer COUNT = Integer.getInteger("count", 1_0);

    private static List<Integer> list;

    @BeforeAll
    static void setup() {
        list = new ArrayList<>(COUNT);
        for (int c = 1; c <= COUNT; c++) {
            list.add(c);
        }
        Collections.shuffle(list);
        System.out.println(list);
    }

    @SuppressWarnings("Не безопасный")
    @Test
    @DisplayName("Последовательный стрим")
    void testSeq() throws NoSuchElementException {
        System.err.println(COUNT);

        Integer integer = list.stream().reduce(Math::max).get();

        System.out.println(integer);

        assertEquals(COUNT, integer);
    }

    @Test
    @SuppressWarnings("Безопасный")
    void maxOptional() {
        List<Integer> list = new ArrayList<>();
        Optional<Integer> maxValue = list.stream().max(Integer::compareTo);

//        Integer integer = maxValue.orElse(null);
//        System.err.println(integer);

        List<Integer> list2 = new ArrayList<>();

        maxValue.ifPresent(System.err::println);
        maxValue.ifPresent(list2::add);

        System.out.println("Максимум от пустого списка = " + maxValue);
    }

    @Test
    @SuppressWarnings("Безопасный")
    void maxWithNull() {
        ArrayList<Integer> testValuesNull = new ArrayList();
        testValuesNull.add(0, null);
        testValuesNull.add(1, 1);
        testValuesNull.add(2, 2);
        testValuesNull.add(3, 70);
        testValuesNull.add(4, 50);

        Optional<Integer> maxValueNotNull = testValuesNull
                .stream()
                .filter((p) -> p != null)
                .max(Integer::compareTo);

        System.out.println("maxValueNotNull= " + maxValueNotNull);
    }

    @Test
    @DisplayName("Паралельный стрим")
    void testPar() {
        System.err.println(COUNT);

        Integer integer = list.parallelStream().reduce(Math::max).get();

        System.out.println(integer);

        assertEquals(COUNT, integer);
    }

}
