package ru.steklopod.tv.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.steklopod.tv.entities.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository

public class MapDAO {
    private static final Logger logger = LoggerFactory.getLogger(MapDAO.class);

    @Autowired
    @Qualifier("TrCheck")
    private DataSource dataSource;

    public List<Status> gettvtatus() {
        String sql = "SELECT Tvera, status FROM stat_tv.get_active_Tvera_stat()";
        List<Status> statusList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareCall(sql);
            ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                String Tvera = rs.getString(1);
                int status = rs.getInt(2);
                Status stat = new Status(Tvera, status);
                statusList.add(stat);
            }
            prepareStatement.close();
            connection.commit();
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при обращении к базе данных в момент получения статуса. ");
        }
        logger.info("Информация о статусах получена из БД.");
        return statusList;

    }

}
