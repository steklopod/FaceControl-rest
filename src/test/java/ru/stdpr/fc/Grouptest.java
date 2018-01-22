package ru.stdpr.fc;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stdpr.fc.entities.GroupDiction;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.GroupDAO;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Rollback
class Grouptest {
    private static Logger logger = LoggerFactory.getLogger(Grouptest.class);

    @Autowired
    GroupDAO groupDAO;

    @Autowired
    CameraDAO cameraDAO;

    @Autowired
    @Qualifier("FaceControlDC")
    DataSource dataSource;

    @Test
    @RepeatedTest(100)
    void getGroups() throws SQLException {
        List<GroupDiction> groups = groupDAO.getGroups();
//        groups.forEach(System.err::println);
    }




}
