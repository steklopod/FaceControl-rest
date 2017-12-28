package ru.stdpr.fc.later;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stdpr.fc.util.CalculateInterface;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class Lambda3 {

    public static void evaluate(List<Integer> list, Predicate<Integer> predicate) {
        for (Integer n : list) {
            if (predicate.test(n)) {
                System.out.print(n + " ");
            }
        }
        System.out.println();
    }

    @Test
    void tretiy() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        System.out.print("Выводит все числа: ");
        evaluate(list, (n) -> true);

        System.out.print("Не выводит ни одного числа: ");
        evaluate(list, (n) -> false);

        System.out.print("Вывод четных чисел: ");
        evaluate(list, (n) -> n % 2 == 0);

        System.out.print("Вывод нечетных чисел: ");
        evaluate(list, (n) -> n % 2 == 1);

        System.out.print("Вывод чисел больше 5: ");
        evaluate(list, (n) -> n > 5);
    }
    ///////////////////////////////////////////////////////////////////////////////

    @Test
    void interfaceFuncCulc() {
        CalculateInterface calculateInterface;

        calculateInterface = (x, y) -> x + y;
        int calcilate = calculateInterface.calcilate(2, 3);

        CalculateInterface quadro = (n, m) -> n * m;
        int calcilate2 = quadro.calcilate(2, 3);

        System.err.println(calcilate);
        System.out.println(calcilate2);
    }


    ///////////////////////////////////////////////////////////////////////////////
    @Test
    void chetvertiyOld() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        for (Integer n : list) {
            int x = n * n;
            System.out.println(x);
        }
    }

    @Test
    void chetvertiyNew() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        list
                .stream()
                .map((x) -> x * x)
                .forEach(System.out::println);
    }

    @Test
    void fiveOld() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        int sum = 0;
        for (Integer n : list) {
            int x = n * n;
            sum = sum + x;
        }
        System.out.println(sum);
    }

    @Test
    void fiveNew() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        int sum = list.stream()
                .map(x -> x * x)
                .reduce((x, y) -> x + y)
                .get();
        System.out.println(sum);
    }

    @Test
    void runnable() {
        Runnable runnable = () -> {
            for (int i = 0; i < 5; i++) System.out.println(i);
        };
        runnable.run();
    }
}
