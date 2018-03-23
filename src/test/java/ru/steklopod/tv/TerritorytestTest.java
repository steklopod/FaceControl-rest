package ru.steklopod.tv;

import org.junit.jupiter.api.*;
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
import ru.steklopod.tv.entities.ocrugDiction;
import ru.steklopod.tv.repository.TverDAO;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Rollback
@Disabled
class ocrugtestTest {
    private static Logger logger = LoggerFactory.getLogger(ocrugtestTest.class);

    @Autowired
    @Qualifier("AppDC")
    DataSource dataSource;
    @Autowired
    TverDAO TverDAO;



    @Test
    @DisplayName("Названия территорий")
    void getTerrNames() {
        List<ocrugDiction> territories = TverDAO.getTerritories();
        System.err.println(territories);
    }

    @Test
    @Rollback
    void updateocrugWithExistingid() throws SQLException {
        ocrugDiction ocrugDiction = TverDAO.getTerritories().stream().findFirst().get();
        System.out.println("" + ocrugDiction);

        String name = ocrugDiction.getName();
        System.out.println("Найденная Место: " + name);

        ocrugDiction.setOldId(ocrugDiction.getId());
//        ocrugDiction.setId(new BigDecimal("666"));
        ocrugDiction.setName(name + "-тест");

        TverDAO.updateocrug(ocrugDiction);
        ocrugDiction.setName(name);

        try {
            TverDAO.updateocrug(ocrugDiction);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>> Fuck");
        }
    }


}
