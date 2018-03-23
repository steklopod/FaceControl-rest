package ru.steklopod.tv;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.tv.entities.Tvera;
import ru.steklopod.tv.entities.ocrug;
import ru.steklopod.tv.repository.TverDAO;
import ru.steklopod.tv.repository.GroupDAO;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Rollback
public class tvTest {
    private static Logger logger = LoggerFactory.getLogger(tvTest.class);

    @Autowired
    GroupDAO groupDAO;

    @Autowired
    TverDAO TverDAO;

    @Autowired
    @Qualifier("AppDC")
    DataSource dataSource;

    List<Tvera> tv = new ArrayList<>();



    @Test
    @DisplayName("Обновляем с исключением")
    @Disabled
    void updateTveraWithException() throws SQLException {
        List<Tvera> tv = TverDAO.getAlltv();
        Tvera firstTveraInList = tv.get(0);
        System.out.println(firstTveraInList);
        String id = firstTveraInList.getId();
        String name = firstTveraInList.getName();

        Throwable exception = assertThrows(Exception.class, () -> {
            TverDAO.updateTvera(new Tvera(id, id, name + "-тест"));
        });
    }

    @Test
    @DisplayName("Обновляем камеру")
    @Disabled
    void updateTvera() throws Exception {
        List<Tvera> tv = TverDAO.getAlltv();
        Tvera firstTveraInList = tv.get(0);
        System.out.println(firstTveraInList);
        String id = firstTveraInList.getId();
        String name = firstTveraInList.getName();

        Tvera Tvera = new Tvera(id, id, name + "-тест");
        System.err.println(Tvera);

        TverDAO.updateTvera(Tvera);
    }

    @Test
    @DisplayName("Проверка JSON")
    @Disabled
    void testJSON() throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ocrug> alltv = TverDAO.gettvJSON();
    }

    @Test
    @Transactional
    @Disabled
    void createNewTvera() throws SQLException {
        Tvera choosenTvera = new Tvera();
        String statusOfUpdate = TverDAO.createTvera(choosenTvera);
        System.err.println("statusOfUpdate = " + statusOfUpdate);
    }

    @Test
    @Disabled
    @DisplayName("Ожидаем исключение")
    void delete() {
        String id = "Не существующий id";
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            TverDAO.deleteTvera(id);
        });
//        assertEquals("a message", exception.getMessage());
    }

    @Test
    void getAlltv() throws SQLException {
        List<Tvera> alltv = TverDAO.getAlltv();
//        alltv.forEach(System.err::println);
    }

    @Test
//    @RepeatedTest(600)
    void gettvTree() throws SQLException {
        List<ocrug> tvTree = TverDAO.gettvTree();
//        System.err.println(tvTree);
    }

    @Test
    @DisplayName("Проверка получения из БД")
    void printValue() throws SQLException {
        List<ocrug> alltv = TverDAO.gettvJSON();
        System.err.println(alltv);
    }


}
