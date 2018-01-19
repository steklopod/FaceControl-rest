package ru.stdpr.fc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stdpr.fc.entities.TerritoryDiction;
import ru.stdpr.fc.repository.TerritoryDAO;

import java.util.List;

@RestController
@CrossOrigin
public class TerritoryController {
    private static final Logger logger = LoggerFactory.getLogger(TerritoryController.class);

    @Autowired
    TerritoryDAO territoryDAO;

    @GetMapping(value = "/getTerritories")
    public List<TerritoryDiction> getTerritories() {
        List<TerritoryDiction> territories = territoryDAO.getTerritories();
        return territories;
    }


    @PostMapping(value = "/updateTerritory")
    public ResponseEntity updateTerritory(@RequestBody TerritoryDiction territoryDiction) {
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).build();
        try {
            territoryDAO.updateTerritory(territoryDiction);
            return responseEntity;
        } catch (Exception e) {
            logger.warn("Ошибка при попытке обновления");
            System.err.println(e);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e);
        }
    }


}
