package ru.stdpr.fc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stdpr.fc.entities.ChoosenCamera;
import ru.stdpr.fc.entities.KeyWords;
import ru.stdpr.fc.entities.Territory;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.KeywordsDAO;


import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
public class FaceControlTest {
    private static Logger logger = LoggerFactory.getLogger(FaceControlTest.class);

    @Autowired
    CameraDAO cameraDAO;
    @Autowired
    KeywordsDAO keywordsDAO;
    @Autowired
    DataSource dataSource;


    @Test
    @DisplayName("Проверка получения из БД")
    void printValue() {
        List<Territory> extraPhotos = cameraDAO.getAllCameras();
        System.err.println(extraPhotos);
    }

    @Test
    void testKeywords() throws IOException {
        List<ChoosenCamera> choosenCameras = keywordsDAO.selectAllCameras();
        HashSet<String> strings = keywordsDAO.convertObjectToString(choosenCameras);

        System.err.println(strings.size());
        System.err.println(strings);
    }

    @Test
    void insertKeywords() {
        keywordsDAO.parseKeywords();
    }

    @Test
    void getCamerasId() {
        Set<KeyWords> ids = keywordsDAO.getKeyWords();
        System.err.println(ids);
    }

    @Test
    @DisplayName("Проверка JSON")
    @Disabled
    void testJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Territory> extraPhotos = cameraDAO.getAllCameras();
    }

    @Test
    @Transactional
    void createNewCamera() {
        ChoosenCamera choosenCamera = new ChoosenCamera("1-junit-test", "террит-тест",
                "group-тест", "34.5555534    55.00", "comment--тест",
                new BigDecimal("33.00"), new BigDecimal("360"));
        String statusOfUpdate = cameraDAO.createCamera(choosenCamera);
        System.err.println("statusOfUpdate = " + statusOfUpdate);
    }

    @Test
//    @Disabled
    @DisplayName("Ожидаем исключение")
    void delete(){
        String id = "Не существующий id";
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            cameraDAO.deleteCamera(id);
        });
//        assertEquals("a message", exception.getMessage());
    }

    @Test
    void encode() throws UnsupportedEncodingException {
        String first = "-ÐºÐ¾Ð¿Ð¸Ñ\u008F ";
        String second = "-%D0%BA%D0%BE%D0%BF%D0%B8%D1%8F";

        String decode1 = new String(first.getBytes("ISO-8859-1"), "UTF-8");
        String decode2 = URLDecoder.decode(second, "UTF-8");

        System.err.println(decode1);
        System.err.println(decode2);

    }

    @Test
    void testConverting() {
        ChoosenCamera newCamera = new ChoosenCamera();
        newCamera.setChoosenCoordinates("  55.753963  , 37.620330   ");
        String[] choosenCoordinates = newCamera.getChoosenCoordinates().trim().split("\\s*(=>|,|\\s)\\s*");
        if (choosenCoordinates.length != 2) {
            logger.warn("После обработки координт должно быть 2 объекта.");

        } else {
            BigDecimal latitude = new BigDecimal(choosenCoordinates[0]);
            BigDecimal longitude = new BigDecimal(choosenCoordinates[1]);
            logger.info(String.valueOf(latitude));
            logger.info(String.valueOf(longitude));
        }
    }

    @Test
    @Transactional
    @Disabled
    void testSqlInsert(){
        ChoosenCamera newCamera = new ChoosenCamera("1-junit-test", "террит-тест",
                "group-тест", "34.5555534    55.00", "comment--тест",
                new BigDecimal("33.00"), new BigDecimal("360"));

        String sql = "SELECT face_control.create_new_camera(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try (Connection connection = dataSource.getConnection()) {

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newCamera.getId());
            preparedStatement.setString(2, newCamera.getChoosenTerritory());
            preparedStatement.setString(3, newCamera.getChoosenGroup());

            String[] choosenCoordinates = newCamera.getChoosenCoordinates().trim().split("\\s+");

            if (choosenCoordinates.length != 2) {
                preparedStatement.setNull(4, Types.NUMERIC);
                preparedStatement.setNull(5, Types.NUMERIC);
            } else {
                BigDecimal latitude = new BigDecimal(choosenCoordinates[0]);
                BigDecimal longitude = new BigDecimal(choosenCoordinates[1]);
                preparedStatement.setBigDecimal(4, latitude);
                preparedStatement.setBigDecimal(5, longitude);
            }
            preparedStatement.setString(6, newCamera.getComment());
            preparedStatement.setBigDecimal(7, newCamera.getProcentsOfRecognize());
            preparedStatement.setBigDecimal(8, newCamera.getChoosenAzimut());
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
