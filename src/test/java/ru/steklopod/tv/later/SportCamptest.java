package ru.steklopod.tv.later;

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
public class SportTvptest {

    @Test
//    Поиск имени самого большого по продолжительности нахождения в лагере
    void sportTvp() {
        Collection<SportsTvp> sport = Arrays.asList(
                new SportsTvp("Ivan", 5),
                new SportsTvp("Petr", 7),
                new SportsTvp("Ira", 10)
        );

        String name = sport
                .stream()
                .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                .get()
                .getName();

        // То же самое
        String name2 = sport
                .stream()
                .max(Comparator.comparing(SportsTvp::getDay))
                .get()
                .getName();

        System.out.println("больше всех пробыл в лагере -" + name);

        String min = sport.stream().min(Comparator.comparing(SportsTvp::getDay)).get().getName();
        System.err.println("меньше всех пробыл в лагере - " + min);

        assertEquals(name, name2);
    }

    @Test
    void sportTvpWithNull() {
        Collection<SportsTvp> sport = Arrays.asList(
                new SportsTvp("Ivan", 5),
                new SportsTvp( null, 15),
                new SportsTvp("Petr", 7),
                new SportsTvp("Ira", 10)
        );

        String nameTest =
                sport.stream()
                        .filter((p) -> p.getName() != null)
                        .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                        .get()
                        .getName();
    }

}


