package ru.stdpr.fc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stdpr.fc.entities.*;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.KeywordsDAO;
import ru.stdpr.fc.repository.MapDAO;
import ru.stdpr.fc.repository.TerritoryDAO;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
public class CamerasController {
    private static final Logger logger = LoggerFactory.getLogger(CamerasController.class);

    @Autowired
    CameraDAO cameraDAO;
    @Autowired
    KeywordsDAO keywordsDAO;
    @Autowired
    MapDAO mapDAO;
    @Autowired
    TerritoryDAO territoryDAO;

    @Value("${classes.start.with}")
    String classStartWith;

    @GetMapping(value = "/getCameras")
    public List<Territory> getCameras() {
        List<Territory> extraPhotos = null;
        try {
            extraPhotos = cameraDAO.getCamerasTree();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extraPhotos;
    }

    @GetMapping(value = "/getAllCameras")
    public List<Camera> getAllCameras() {
        List<Camera> allCameras = null;
        try {
            allCameras = cameraDAO.getAllCameras();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCameras;
    }

    @GetMapping(value = "/getGroups")
    public List<GroupDiction> getGroups() {
        List<GroupDiction> groups = null;
        try {
            groups = cameraDAO.getGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @PostMapping(value = "/updateCamera")
    public ResponseEntity updateCamera(@RequestBody Camera camera) {
        try {
            cameraDAO.updateCamera(camera);
        } catch (Exception e) {
            Exception exception = parseException(e);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(exception);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/createCamera")
    public ResponseEntity createCamera(@RequestBody Camera camera) {
        try {
//            logger.info("camera = " + String.valueOf(camera.getId()));
            String status = cameraDAO.createCamera(camera);
            if (status.equals("OK")) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @GetMapping(value = "/getKeywords")
    public Set<KeyWords> getKeywords() {
        Set<KeyWords> keyWordsObjects = keywordsDAO.getKeyWords();
        return keyWordsObjects;
    }


    @RequestMapping(value = "/deleteCamera/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCamera(@PathVariable String id) {
        try {
            String decodeId = new String(id.getBytes("ISO-8859-1"), "UTF-8");
            logger.info("Получен id: " + id + " для удаления." + "| Id после декодирования: " + decodeId + ".");
            cameraDAO.deleteCamera(decodeId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
//            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("Первая версия, устаревшая")
    @GetMapping(value = "/getCamerasTree")
    public List<Territory> getCamerasTree() {
        List<Territory> extraPhotos = null;
        try {
            extraPhotos = cameraDAO.getCamerasJSON();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extraPhotos;
    }

    @GetMapping(value = "/getCameraStatus")
    public List<Status> getCameraStatus() {
        List<Status> cameraStatusList = mapDAO.getCameraStatus();
        return cameraStatusList;
    }

    public Exception parseException(Exception e){
        String localizedMessage = e.getMessage();
        String[] wheres = localizedMessage.split("Where");
        String reason;
        if (wheres.length > 1) {
            reason = wheres[0];
        } else {
            reason = localizedMessage;
        }
        StackTraceElement[] mistakes = Arrays.stream(e.getStackTrace())
                .filter(s -> s.getClassName().startsWith(classStartWith) && !s.getClassName().contains("$"))
                .toArray(StackTraceElement[]::new);
        Exception exception = new Exception(reason);
        exception.setStackTrace(mistakes);
        exception.printStackTrace();
        return exception;
    }

}
