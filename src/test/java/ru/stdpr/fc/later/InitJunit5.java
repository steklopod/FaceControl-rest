package ru.stdpr.fc.later;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class InitJunit5 {

    private static Logger logger = LoggerFactory.getLogger(InitJunit5.class);

    @Value("${pool.connection.timeout}")
    private String timeout;


    @BeforeAll
    static void initializeExternalResources() {
        System.out.println("Перед всеми...");
    }

    @BeforeEach
    void initializeMockObjects() {
        System.out.println("Перед каждым...");
    }


    @Test
    @DisplayName("Проверка доступности application.properties")
    void printValue() {
        System.err.println(timeout);
    }

    @DisplayName("😱 Повторяемый тест")
    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    void someTest() {
        System.out.println("Повторяемый тест...");
        assertTrue(true);
    }

    @RepeatedTest(4)
    void otherTest() {
        assumeTrue(true);
        System.out.println("Другой повторяемый...");
        assertNotEquals(1, 42, "Why wouldn't these be the same?");
    }

    @Test
    @Disabled
    void disabledTest() {
        System.exit(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testWithValueSource(int argument) {
        assertNotNull(argument);
    }


    @AfterEach
    void tearDown() {
        System.out.println("После каждого метода...");
    }

    @AfterAll
    static void freeExternalResources() {
        System.out.println("И, наконец, после всех..");
    }

}
