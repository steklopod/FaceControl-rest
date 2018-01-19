package ru.stdpr.fc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stdpr.fc.entities.TerritoryDiction;

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
public class TerritoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    @Qualifier("FaceControlDC")
    private DataSource dataSource;

    @Value("${nameOfEmptyTerritory}")
    private String nameOfEmptyTerritory;

    public List<TerritoryDiction> getTerritories() {
        List<TerritoryDiction> territoryList = new ArrayList<>();

        String sql = "SELECT * FROM face_control.s_camera_territory";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">>>GET запрос на получение территорий: " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("territory_id");
                String territoryName = rs.getString("territory_name");
                if (territoryName == null) {
                    territoryName = "Без имени";
                }
                String territoryDefine = rs.getString("territory_define");
                TerritoryDiction territoryDiction = new TerritoryDiction(id, territoryName, territoryDefine);
                territoryList.add(territoryDiction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<TerritoryDiction> filteredTerritories = territoryList.stream()
                .distinct()
                .sorted(Comparator.comparing(TerritoryDiction::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        filteredTerritories.add(new TerritoryDiction(getNameOfEmptyTerritory()));
        return filteredTerritories;
    }

    public void updateTerritory(TerritoryDiction territoryDiction) throws SQLException {
        String sql = "SELECT * FROM face_control.update_territory(?,?,?,?)";
        BigDecimal oldId = territoryDiction.getOldId();
        BigDecimal id = territoryDiction.getId();
        String name = territoryDiction.getName();
        String define = territoryDiction.getDefine();
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


    private String getNameOfEmptyTerritory() {
        String decode = null;
        try {
            decode = new String(nameOfEmptyTerritory.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }


}
