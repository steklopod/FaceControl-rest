package ru.steklopod.tv.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.tv.entities.ocrugDiction;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TverDAO {
    private static final Logger logger = LoggerFactory.getLogger(TverDAO.class);

    @Autowired
    @Qualifier("AppDC")
    private DataSource dataSource;

    @Value("${nameOfEmptyocrug}")
    private String nameOfEmptyocrug;

    public List<ocrugDiction> getTerritories() {
        List<ocrugDiction> ocrugList = new ArrayList<>();

        String sql = "SELECT * FROM database.s_Tvera_ocrug";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">GET запрос на получение территорий: " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("ocrug_id");
                String ocrugName = rs.getString("ocrug_name");
                if (ocrugName == null) {
                    ocrugName = "Без имени";
                }
                String ocrugDefine = rs.getString("ocrug_define");
                ocrugDiction ocrugDiction = new ocrugDiction(id, ocrugName, ocrugDefine);
                ocrugList.add(ocrugDiction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<ocrugDiction> filteredTerritories = ocrugList.stream()
                .distinct()
                .sorted(Comparator.comparing(ocrugDiction::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        filteredTerritories.add(new ocrugDiction(getNameOfEmptyocrug()));
        return filteredTerritories;
    }

    public void updateocrug(ocrugDiction ocrugDiction) throws SQLException {
        String sql = "SELECT * FROM database.update_ocrug(?,?,?,?)";
        BigDecimal oldId = ocrugDiction.getOldId();
        BigDecimal id = ocrugDiction.getId();
        String name = ocrugDiction.getName();
        String define = ocrugDiction.getDefine();
        PreparedStatement prepareStatement;

        try (Connection connection = dataSource.getConnection()) {
            prepareStatement = connection.prepareStatement(sql);
            prepareStatement.setBigDecimal(1, oldId);
            prepareStatement.setBigDecimal(2, id);
            prepareStatement.setString(3, name);
            prepareStatement.setString(4, define);
            prepareStatement.execute();
            prepareStatement.close();
            logger.info("Данные для территории с ID " + oldId + " успешно обновлены.");
        }
    }


    private String getNameOfEmptyocrug() {
        String decode = null;
        try {
            decode = new String(nameOfEmptyocrug.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }


}
