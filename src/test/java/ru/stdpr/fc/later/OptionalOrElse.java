package ru.stdpr.fc.later;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stdpr.fc.connection.DataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ContextConfiguration(classes = {DataProvider.class})
class OptionalOrElse {
    private static Logger logger = LoggerFactory.getLogger(OptionalOrElse.class);

    @Test
    void replaceAllNull(){
        List<String> list = Arrays.asList("a", "b", null, "c", "d", null);
        list.replaceAll(s -> s == null ? "x" : s);
        System.out.println(list);
    }

    @Test
    void whenOrElseGetWorks_thenCorrect() {
        String nullName = null;
        System.out.println("name до = " + nullName);
        String name = Optional.ofNullable(nullName).orElse("john");
        System.out.println("name после orElse = " + nullName);
        assertEquals("john", name);
    }

    String getMyDefault() {
        System.err.println("    >>> Зашли внутрь дефолтного метода");
        return "Default Value";
    }

    @Test
    void whenOrElseIsNull() {
        String text = null;
        logger.info("Т.к. значение = null - выполнятся оба. Разницы нет.");

        System.out.println("Using orElseGet:");
        String defaultText =
                Optional.ofNullable(text).orElseGet(this::getMyDefault);
        assertEquals("Default Value", defaultText);

        System.out.println("Using orElse:");
        defaultText =
                Optional.ofNullable(text).orElse(getMyDefault());
        assertEquals("Default Value", defaultText);
    }

    @Test
    void whenOrElseIsNotNull() {
        String text = "Text present";
        logger.info("Т.к. значение != null - выполнится только orElse.");

        System.err.println("orElseGet: не зайдем внутрь.");
        String defaultText
                = Optional.ofNullable(text).orElseGet(this::getMyDefault);
        assertEquals("Text present", defaultText);

        System.out.println("orElse: зайдем в любом случае");
        defaultText = Optional.ofNullable(text).orElse(getMyDefault());
        assertEquals("Text present", defaultText);
    }

    @Test
    void orElseThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String nullName = null;
            String name = Optional.ofNullable(nullName).orElseThrow(
                    IllegalArgumentException::new);
        });
    }


}