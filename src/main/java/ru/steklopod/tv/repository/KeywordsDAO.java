package ru.steklopod.tv.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.steklopod.tv.entities.ChoosenTvera;
import ru.steklopod.tv.entities.KeyWords;

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

        String sql = "SELECT get_tv_id_keywords cur FROM database.get_tv_id_keywords()";
        Set<KeyWords> keyWords = new HashSet<>();

        Set<String> territories = new HashSet<String>();
        Set<String> groups = new HashSet<String>();
        Set<String> tv = new HashSet<String>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet retvursor = (ResultSet) rs.getObject("cur");
            while (retvursor.next()) {
                String ocrug = retvursor.getString("name");
                String group = retvursor.getString("place_text");
                String id = retvursor.getString("Tvera");
                if (ocrug != null) {
                    territories.add(ocrug);
                }
                if (group != null) {
                    groups.add(group);
                }
                if (id != null) {
                    tv.add(id);
                }
            }
            for (String t : territories) {
                KeyWords ter = new KeyWords(t, t, "gps_fixed", "Место");
                keyWords.add(ter);
            }
            for (String gr : groups) {
                KeyWords group = new KeyWords(gr, gr, "filter_none", "местаа");
                keyWords.add(group);
            }
            for (String c : tv) {
                KeyWords Tv = new KeyWords(c, c, "videoTv", "Сущность");
                keyWords.add(Tv);
            }
            keyWords.add(new KeyWords("Место не задана", "Место не задана", "gps_fixed", "Место"));
            keyWords.add(new KeyWords("без местаы", "без местаы", "filter_none", "местаа"));
        } catch (SQLException e) {
            logger.error("Ошибка при пакетной обработке (попытка удаления).");
            e.printStackTrace();
        }
        territories.clear();
        groups.clear();
        tv.clear();
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

//TODO - заменить класс ChoosenTvera на класс Tvera
    public List<ChoosenTvera> selectAlltv() {
        String sql = "SELECT get_tv cur FROM database.get_tv()";
        List<ChoosenTvera> tvList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            rs.next();
            ResultSet retvursor = (ResultSet) rs.getObject("cur");
            while (retvursor.next()) {
                String ocrugName = retvursor.getString("name");
                if (ocrugName == null) {
                    ocrugName = "Место не задана";
                }
                String groupName = retvursor.getString("place_text");
                if (groupName == null) {
                    groupName = "без местаы";
                }
                String id = retvursor.getString("Tvera");
                BigDecimal azimut = retvursor.getBigDecimal("azimut");
                BigDecimal recognizePercent = retvursor.getBigDecimal("min_proc");
                String comment = retvursor.getString("note");
                BigDecimal longitude = retvursor.getBigDecimal("longitude");
                BigDecimal latitude = retvursor.getBigDecimal("latitude");

                String coordinates = null;
                if (longitude != null && latitude != null) {
                    coordinates = String.valueOf(longitude) + " " + String.valueOf(latitude);
                }
                ChoosenTvera Tvera = new ChoosenTvera(id, ocrugName, groupName, azimut, coordinates, recognizePercent);
                tvList.add(Tvera);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tvList;
    }


    public HashSet<String> parseKeywords() {
        List<ChoosenTvera> choosentv = selectAlltv();

        HashSet<String> keyWords = convertObjectToString(choosentv);
        deleteAll();
        String prepareSQL = "SELECT database.insert_tv_keywords(?)";
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


    public HashSet<String> convertObjectToString(List<ChoosenTvera> choosentv) {
        HashSet<String> keyWords = new HashSet<>();
        for (ChoosenTvera Tv : choosentv) {
            HashSet<String> TvString = convertOneTvToStringSet(Tv);
            keyWords.addAll(TvString);
        }
        return keyWords;
    }

    private HashSet<String> convertOneTvToStringSet(ChoosenTvera c) {
        HashSet<String> strings = new HashSet<>();

        strings.add(c.getId());
        strings.add(c.getChoosenocrug());
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
        String prepareSQL = "TRUNCATE TABLE database.table_keywords";
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
