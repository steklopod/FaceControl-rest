package ru.stdpr.fc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.stdpr.fc.connection.DataProvider;
import ru.stdpr.fc.entities.Territory;
import ru.stdpr.fc.entities.TerritoryDiction;
import ru.stdpr.fc.repository.CameraDAO;
import ru.stdpr.fc.repository.TerritoryDAO;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Rollback
class TerritorytestTest {
    private static Logger logger = LoggerFactory.getLogger(TerritorytestTest.class);

    @Autowired
    @Qualifier("FaceControlDC")
    DataSource dataSource;
    @Autowired
    TerritoryDAO territoryDAO;

    List<TerritoryDiction> territories = new ArrayList<>();

    @BeforeAll
    void makeList() throws SQLException {
        List<TerritoryDiction> territories = territoryDAO.getTerritories();
        this.territories = territories;
    }


    @Test
    @DisplayName("Названия территорий")
    void getTerrNames() {
        List<TerritoryDiction> territories = territoryDAO.getTerritories();
        System.err.println(territories);
    }

    @Test
    @Rollback
    void updateTerritoryWithExistingid() throws SQLException {
        TerritoryDiction territoryDiction = territoryDAO.getTerritories().stream().findFirst().get();
        System.out.println("" + territoryDiction);

        String name = territoryDiction.getName();
        System.out.println("Найденная территория: " + name);

        territoryDiction.setOldId(territoryDiction.getId());
//        territoryDiction.setId(new BigDecimal("666"));
        territoryDiction.setName(name + "-тест");

        territoryDAO.updateTerritory(territoryDiction);
        territoryDiction.setName(name);

        try {
            territoryDAO.updateTerritory(territoryDiction);
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>> Fuck");
        }
    }

    @AfterAll
    void clearList(){
        this.territories.clear();
    }

}
