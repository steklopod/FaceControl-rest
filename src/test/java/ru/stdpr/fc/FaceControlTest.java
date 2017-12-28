package ru.stdpr.fc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stdpr.fc.entities.ChoosenCamera;
import ru.stdpr.fc.entities.KeyWords;
import ru.stdpr.fc.entities.Territory;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.KeywordsDAO;


import java.io.IOException;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
public class FaceControlTest {
    private static Logger logger = LoggerFactory.getLogger(FaceControlTest.class);

    @Autowired
    CameraDAO cameraDAO;
    @Autowired
    KeywordsDAO keywordsDAO;

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


}
