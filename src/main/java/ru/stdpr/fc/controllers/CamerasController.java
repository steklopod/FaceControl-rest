package ru.stdpr.fc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stdpr.fc.entities.ChoosenCamera;
import ru.stdpr.fc.entities.KeyWords;
import ru.stdpr.fc.entities.Territory;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.KeywordsDAO;

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

    @GetMapping(value = "/getCameras")
    public List<Territory> getCameras() {
        List<Territory> extraPhotos = cameraDAO.getAllCameras();
        return extraPhotos;
    }

    @RequestMapping(value = "/updateCamera", method = RequestMethod.POST)
    public ResponseEntity<String> updateCamera(@RequestBody ChoosenCamera camera) {
        try {
            cameraDAO.updateCamera(camera);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/createCamera", method = RequestMethod.POST)
    public ResponseEntity<String> createCamera(@RequestBody ChoosenCamera camera) {
        try {
            String status = cameraDAO.createCamera(camera);
            if (status.equals("OK")) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @GetMapping(value = "/getKeywords")
    public Set<KeyWords> getKeywords() {
        Set<KeyWords> keyWordsObjects = keywordsDAO.getKeyWords();
        return keyWordsObjects;
    }

    @RequestMapping(value = "/deleteCamera/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCamera(@PathVariable String id) {
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


}
