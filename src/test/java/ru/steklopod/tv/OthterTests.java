package ru.steklopod.tv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.steklopod.tv.entities.Tvera;
import ru.steklopod.tv.entities.ChoosenTvera;
import ru.steklopod.tv.entities.KeyWords;
import ru.steklopod.tv.entities.Status;
import ru.steklopod.tv.repository.KeywordsDAO;
import ru.steklopod.tv.repository.MapDAO;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
class OthterTests {
    private static Logger logger = LoggerFactory.getLogger(OthterTests.class);

    @Autowired
    KeywordsDAO keywordsDAO;
    @Autowired
    MapDAO mapDAO;


    @Test
    void getStatusList() {
        List<Status> tvtatusList = mapDAO.gettvtatus();
        System.err.println(tvtatusList);
    }

    @Test
    void insertKeywords() {
        keywordsDAO.parseKeywords();
    }

    @Test
    void testKeywords() {
        List<ChoosenTvera> choosentv = keywordsDAO.selectAlltv();
        HashSet<String> strings = keywordsDAO.convertObjectToString(choosentv);

        System.err.println(strings.size());
        System.err.println(strings);
    }

    @Test
    void gettvId() {
        Set<KeyWords> ids = keywordsDAO.getKeyWords();
        System.err.println(ids);
    }


    @Test
    void encode() throws UnsupportedEncodingException {
        String first = "-ÐºÐ¾Ð¿Ð¸Ñ\u008F ";
        String second = "-%D0%BA%D0%BE%D0%BF%D0%B8%D1%8F";

        String decode1 = new String(first.getBytes("ISO-8859-1"), "UTF-8");
        String decode2 = URLDecoder.decode(second, "UTF-8");

        System.err.println(decode1);
        System.err.println(decode2);

    }

    @Test
    void testConverting() {
        Tvera newTvera = new Tvera();
        newTvera.setCoordinates("37.41475, 55.81977");
        String[] choosenCoordinates = newTvera.getCoordinates().trim().split("\\s*(=>|,|\\s)\\s*");
        if (choosenCoordinates.length != 2) {
            logger.warn("После обработки координт должно быть 2 объекта.");

        } else {
            BigDecimal latitude = new BigDecimal(choosenCoordinates[0]);
            BigDecimal longitude = new BigDecimal(choosenCoordinates[1]);
            logger.info(String.valueOf(latitude));
            logger.info(String.valueOf(longitude));
        }
    }

    @Test
    void testString() {
        String a = "qwe";
        a = "@@@@@@@@@@";
        System.err.println(a);
    }


}
