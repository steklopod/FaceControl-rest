package ru.stdpr.fc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.stdpr.fc.entities.ChoosenCamera;
import ru.stdpr.fc.entities.KeyWords;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class KeywordsDAO {

    private static final Logger logger = LoggerFactory.getLogger(KeywordsDAO.class);

    @Autowired
    private DataSource dataSource;


    public Set<KeyWords> getKeyWords() {

        String sql = "SELECT get_cameras_id_keywords cur FROM face_control.get_cameras_id_keywords()";
        Set<KeyWords> keyWords = new HashSet<>();

        Set<String> territories = new HashSet<String>();
        Set<String> groups = new HashSet<String>();
        Set<String> cameras = new HashSet<String>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet refCursor = (ResultSet) rs.getObject("cur");
            while (refCursor.next()) {
                String territory = refCursor.getString("name");
                String group = refCursor.getString("place_text");
                String id = refCursor.getString("camera");
                if (territory != null) {
                    territories.add(territory);
                }
                if (group != null) {
                    groups.add(group);
                }
                if (id != null) {
                    cameras.add(id);
                }
            }
            for (String t : territories) {
                KeyWords ter = new KeyWords(t, t, "gps_fixed", "территория");
                keyWords.add(ter);
            }
            for (String gr : groups) {
                KeyWords group = new KeyWords(gr, gr, "filter_none", "группа");
                keyWords.add(group);
            }
            for (String c : cameras) {
                KeyWords cam = new KeyWords(c, c, "videocam", "камера");
                keyWords.add(cam);
            }
            keyWords.add(new KeyWords("территория не задана", "территория не задана", "gps_fixed", "территория"));
            keyWords.add(new KeyWords("без группы", "без группы", "filter_none", "группа"));
        } catch (SQLException e) {
            logger.error("Ошибка при пакетной обработке (попытка удаления).");
            e.printStackTrace();
        }
        territories.clear();
        groups.clear();
        cameras.clear();
        return keyWords;
    }


    /////////////////////////////////////////


    public List<KeyWords> getKeyWordsObjects() {
        List<KeyWords> keyWords = new ArrayList<>();
        HashSet<String> strings = parseKeywords();
        for (String s : strings) {
            KeyWords kwo = new KeyWords(s, s);
            keyWords.add(kwo);
        }
        return keyWords;
    }


    public List<ChoosenCamera> selectAllCameras() {
        String sql = "SELECT get_cameras cur FROM face_control.get_cameras()";
        List<ChoosenCamera> camerasList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet refCursor = (ResultSet) rs.getObject("cur");
            while (refCursor.next()) {
                String territoryName = refCursor.getString("name");
                if (territoryName == null) {
                    territoryName = "территория не задана";
                }
                String groupName = refCursor.getString("place_text");
                if (groupName == null) {
                    groupName = "без группы";
                }
                String id = refCursor.getString("camera");
                BigDecimal azimut = refCursor.getBigDecimal("azimut");
                BigDecimal recognizePercent = refCursor.getBigDecimal("min_proc");
                String comment = refCursor.getString("note");
                BigDecimal longitude = refCursor.getBigDecimal("longitude");
                BigDecimal latitude = refCursor.getBigDecimal("latitude");

                String coordinates = null;
                if (longitude != null && latitude != null) {
                    coordinates = String.valueOf(longitude) + " " + String.valueOf(latitude);
                }
                ChoosenCamera camera = new ChoosenCamera(id, territoryName, groupName, azimut, coordinates, recognizePercent);
                camerasList.add(camera);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return camerasList;
    }


    public HashSet<String> parseKeywords() {
        List<ChoosenCamera> choosenCameras = selectAllCameras();

        HashSet<String> keyWords = convertObjectToString(choosenCameras);
        deleteAll();
        String prepareSQL = "SELECT face_control.insert_cameras_keywords(?)";
        PreparedStatement prepareStatement = null;
        try (Connection connection = dataSource.getConnection()) {
            prepareStatement = connection.prepareStatement(prepareSQL);
            Array array = connection.createArrayOf("VARCHAR", keyWords.toArray());
            prepareStatement.setArray(1, array);
            prepareStatement.execute();
            prepareStatement.close();
            logger.info("Ключевые слова успешно обновлены.");
        } catch (SQLException e) {
            logger.error("Ошибка при пакетной обработке (попытка удаления).");
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return keyWords;
    }


    public HashSet<String> convertObjectToString(List<ChoosenCamera> choosenCameras) {
        HashSet<String> keyWords = new HashSet<>();
        for (ChoosenCamera cam : choosenCameras) {
            HashSet<String> camString = convertOneCamToStringSet(cam);
            keyWords.addAll(camString);
        }
        return keyWords;
    }

    private HashSet<String> convertOneCamToStringSet(ChoosenCamera c) {
        HashSet<String> strings = new HashSet<>();

        strings.add(c.getId());
        strings.add(c.getChoosenTerritory());
        strings.add(c.getChoosenGroup());

//        strings.add(String.valueOf(c.getChoosenAzimut()));
//        strings.add(c.getChoosenCoordinates());

//        BigDecimal procentsOfRecognize = c.getProcentsOfRecognize();
//        if (procentsOfRecognize != null) {
//            strings.add(String.valueOf(procentsOfRecognize));
//        }
        return strings;
    }


    private void deleteAll() {
        String prepareSQL = "TRUNCATE TABLE face_control.s_cameras_keywords";
        PreparedStatement prepareStatement = null;
        try (Connection connection = dataSource.getConnection()) {
            prepareStatement = connection.prepareStatement(prepareSQL);
            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
