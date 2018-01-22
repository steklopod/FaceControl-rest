package ru.stdpr.fc.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.stdpr.fc.entities.GroupDiction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class GroupDAO {

    private static final Logger logger = LoggerFactory.getLogger(CameraDAO.class);

    @Autowired
    @Qualifier("FaceControlDC")
    private DataSource dataSource;

    @Autowired
    TerritoryDAO territoryDAO;

    private String nameOfEmptyGroup = "* Группа не задана";


    public List<GroupDiction> getGroups() throws SQLException {
        List<GroupDiction> groupList = new CopyOnWriteArrayList<>();

        groupList.add(new GroupDiction(nameOfEmptyGroup));

        String sql = "SELECT * FROM face_control.s_camera_group";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">>GET запрос на получение групп:     " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("group_id");
                String groupName = rs.getString("group_name");

                BigDecimal territoryId = rs.getBigDecimal("territory_id");
                String groupDefine = rs.getString("group_define");
                GroupDiction groupDiction = new GroupDiction(id, groupName, territoryId, groupDefine);
                groupList.add(groupDiction);
            }
        }
        List<GroupDiction> filterdGroups = groupList.stream()
                .parallel()
                .distinct()
                .sorted(Comparator.comparing(GroupDiction::getName))
                .collect(Collectors.toList());
//        groupList.clear();
        return filterdGroups;
    }


}
