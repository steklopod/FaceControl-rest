package ru.stdpr.fc;

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
import ru.stdpr.fc.entities.Camera;
import ru.stdpr.fc.entities.GroupDiction;
import ru.stdpr.fc.entities.Territory;
import ru.stdpr.fc.repository.CameraDAO;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Rollback
public class CamerasTest {
    private static Logger logger = LoggerFactory.getLogger(CamerasTest.class);

    @Autowired
    CameraDAO cameraDAO;

    @Autowired
    @Qualifier("FaceControlDC")
    DataSource dataSource;

    List<Camera> cameras = new ArrayList<>();



    @Test
    @DisplayName("Обновляем с исключением")
    @Disabled
    void updateCameraWithException() throws SQLException {
        List<Camera> cameras = cameraDAO.getAllCameras();
        Camera firstCameraInList = cameras.get(0);
        System.out.println(firstCameraInList);
        String id = firstCameraInList.getId();
        String name = firstCameraInList.getName();

        Throwable exception = assertThrows(Exception.class, () -> {
            cameraDAO.updateCamera(new Camera(id, id, name + "-тест"));
        });
    }

    @Test
    @DisplayName("Обновляем камеру")
    @Disabled
    void updateCamera() throws Exception {
        List<Camera> cameras = cameraDAO.getAllCameras();
        Camera firstCameraInList = cameras.get(0);
        System.out.println(firstCameraInList);
        String id = firstCameraInList.getId();
        String name = firstCameraInList.getName();

        Camera camera = new Camera(id, id, name + "-тест");
        System.err.println(camera);

        cameraDAO.updateCamera(camera);
    }

    @Test
    @DisplayName("Проверка JSON")
    @Disabled
    void testJSON() throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Territory> allCameras = cameraDAO.getCamerasJSON();
    }

    @Test
    @Transactional
    @Disabled
    void createNewCamera() throws SQLException {
        Camera choosenCamera = new Camera();
        String statusOfUpdate = cameraDAO.createCamera(choosenCamera);
        System.err.println("statusOfUpdate = " + statusOfUpdate);
    }

    @Test
//    @Disabled
    @DisplayName("Ожидаем исключение")
    void delete() {
        String id = "Не существующий id";
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            cameraDAO.deleteCamera(id);
        });
//        assertEquals("a message", exception.getMessage());
    }

    @Test
    void getGroupsKeyWords() throws SQLException {
        List<GroupDiction> groups = cameraDAO.getGroups();
        groups.forEach(System.err::println);
    }

    @Test
    void getAllCameras() throws SQLException {
        List<Camera> allCameras = cameraDAO.getAllCameras();
//        allCameras.forEach(System.err::println);
    }

    @Test
    void getCamerasTree() throws SQLException {
        List<Territory> camerasTree = cameraDAO.getCamerasTree();
//        System.err.println(camerasTree);
    }

    @Test
    @DisplayName("Проверка получения из БД")
    void printValue() throws SQLException {
        List<Territory> allCameras = cameraDAO.getCamerasJSON();
        System.err.println(allCameras);
    }


}
