package ru.stdpr.fc.later;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class SportCamptest {

    @Test
//    Поиск имени самого большого по продолжительности нахождения в лагере
    void sportCamp() {
        Collection<SportsCamp> sport = Arrays.asList(
                new SportsCamp("Ivan", 5),
                new SportsCamp("Petr", 7),
                new SportsCamp("Ira", 10)
        );

        String name = sport
                .stream()
                .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                .get()
                .getName();

        // То же самое
        String name2 = sport
                .stream()
                .max(Comparator.comparing(SportsCamp::getDay))
                .get()
                .getName();

        System.out.println("больше всех пробыл в лагере -" + name);

        String min = sport.stream().min(Comparator.comparing(SportsCamp::getDay)).get().getName();
        System.err.println("меньше всех пробыл в лагере - " + min);

        assertEquals(name, name2);
    }

    @Test
    void sportCampWithNull() {
        Collection<SportsCamp> sport = Arrays.asList(
                new SportsCamp("Ivan", 5),
                new SportsCamp( null, 15),
                new SportsCamp("Petr", 7),
                new SportsCamp("Ira", 10)
        );

        String nameTest =
                sport.stream()
                        .filter((p) -> p.getName() != null)
                        .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                        .get()
                        .getName();
    }

}


