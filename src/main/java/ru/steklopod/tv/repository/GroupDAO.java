package ru.steklopod.tv.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.steklopod.tv.entities.GroupDiction;

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

    private static final Logger logger = LoggerFactory.getLogger(TverDAO.class);

    @Autowired
    @Qualifier("AppDC")
    private DataSource dataSource;

    @Autowired
    TverDAO TverDAO;

    private String nameOfEmptyGroup = "* местаа не задана";


    public List<GroupDiction> getGroups() throws SQLException {
        List<GroupDiction> groupList = new CopyOnWriteArrayList<>();

        groupList.add(new GroupDiction(nameOfEmptyGroup));

        String sql = "SELECT * FROM database.s_Tvera_group";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            logger.info(">>GET запрос на получение места:     " + sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                BigDecimal id = rs.getBigDecimal("group_id");
                String groupName = rs.getString("group_name");

                BigDecimal ocrugId = rs.getBigDecimal("ocrug_id");
                String groupDefine = rs.getString("group_define");
                GroupDiction groupDiction = new GroupDiction(id, groupName, ocrugId, groupDefine);
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
