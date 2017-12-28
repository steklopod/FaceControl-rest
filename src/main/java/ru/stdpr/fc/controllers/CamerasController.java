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

    @GetMapping(value = "/getKeywords")
    public Set<KeyWords> getKeywords() {

        Set<KeyWords> keyWordsObjects = keywordsDAO.getKeyWords();

        return keyWordsObjects;
    }

}
