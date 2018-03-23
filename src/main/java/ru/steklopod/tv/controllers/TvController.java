package ru.steklopod.tv.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.steklopod.tv.entities.*;
import ru.steklopod.tv.repository.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
public class TvController {
    private static final Logger logger = LoggerFactory.getLogger(TvController.class);

    @Autowired
    KeywordsDAO keywordsDAO;
    @Autowired
    MapDAO mapDAO;
    @Autowired
    TverDAO TverDAO;
    @Autowired
    GroupDAO groupDAO;

    @Value("${classes.start.with}")
    String classStartWith;

    @GetMapping(value = "/gettv")
    public List<Ocrug> gettv() {
        List<Ocrug> extraPhotos = null;
        try {
            extraPhotos = TverDAO.gettvTree();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extraPhotos;
    }

    @GetMapping(value = "/getAlltv")
    public List<Tvera> getAlltv() {
        List<Tvera> alltv = null;
        try {
            alltv = TverDAO.getAlltv();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alltv;
    }

    @GetMapping(value = "/getGroups")
    public List<GroupDiction> getGroups() {
        List<GroupDiction> groups = null;
        try {
            groups = groupDAO.getGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @PostMapping(value = "/updateTvera")
    public ResponseEntity updateTvera(@RequestBody Tvera Tvera) {
        try {
            TverDAO.updateTvera(Tvera);
        } catch (Exception e) {
            Exception exception = parseException(e);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(exception);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/createTvera")
    public ResponseEntity createTvera(@RequestBody Tvera Tvera) {
        try {
//            logger.info("Tvera = " + String.valueOf(Tvera.getId()));
            String status = TverDAO.createTvera(Tvera);
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


    @RequestMapping(value = "/deleteTvera/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteTvera(@PathVariable String id) {
        try {
            String decodeId = new String(id.getBytes("ISO-8859-1"), "UTF-8");
            logger.info("Получен id: " + id + " для удаления." + "| Id после декодирования: " + decodeId + ".");
            TverDAO.deleteTvera(decodeId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
//            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("Первая версия, устаревшая")
    @GetMapping(value = "/gettvTree")
    public List<Ocrug> gettvTree() {
        List<Ocrug> extraPhotos = null;
        try {
            extraPhotos = TverDAO.getTvJSON();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extraPhotos;
    }

    @GetMapping(value = "/gettvtatus")
    public List<Status> gettvtatus() {
        List<Status> tvtatusList = mapDAO.gettvtatus();
        return tvtatusList;
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
