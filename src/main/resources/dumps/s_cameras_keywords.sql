CREATE TABLE table_keywords
(
  label VARCHAR(50),
  value VARCHAR(50),
  id    SERIAL NOT NULL
    CONSTRAINT table_keywords_id_pk
    PRIMARY KEY,
  icon  VARCHAR(20),
  stamp VARCHAR(30)
);

CREATE UNIQUE INDEX table_keywords_label_uindex
  ON table_keywords (label);

CREATE UNIQUE INDEX table_keywords_value_uindex
  ON table_keywords (value);

CREATE UNIQUE INDEX table_keywords_id_uindex
  ON table_keywords (id);

COMMENT ON TABLE table_keywords IS 'ключевые слова для поиска контроля лиц';


INSERT INTO database.table_keywords (label, value, id, icon, stamp) VALUES ('', '', 28081, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('steklopod-office-fl2-Tv3', 'steklopod-office-fl2-Tv3', 28082, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('без местаы', 'без местаы', 28083, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('steklopod-office-Tv4', 'steklopod-office-Tv4', 28084, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('steklopod-office-fl2-Tv1', 'steklopod-office-fl2-Tv1', 28085, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('входовая Сущность', 'входовая Сущность', 28086, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('г.Москва ул.Трифоновская д.2', 'г.Москва ул.Трифоновская д.2', 28087, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Кр. Площадь', 'Кр. Площадь', 28088, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Сущность в самом дульнем углу офиса', 'Сущность в самом дульнем углу офиса', 28089, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 0-1-копия', 'Tv 0-1-копия', 28090, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('CD3-5', 'CD3-5', 28091, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('steklopod-office-Tv-@@@', 'steklopod-office-Tv-@@@', 28092, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('ул. Ташкентская', 'ул. Ташкентская', 28093, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('угловая Сущность', 'угловая Сущность', 28094, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('DE3-8', 'DE3-8', 28095, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Самаркандский б-р', 'Самаркандский б-р', 28096, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Сущность около лестницы на втором этаже', 'Сущность около лестницы на втором этаже', 28097, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp) VALUES ('A2-2', 'A2-2', 28098, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp) VALUES ('A2-0', 'A2-0', 28099, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('окололестничная Сущность', 'окололестничная Сущность', 28100, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 2-2', 'Tv 2-2', 28101, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 2-0', 'Tv 2-0', 28102, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 0-1', 'Tv 0-1', 28103, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('ZEL-876', 'ZEL-876', 28104, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('steklopod-office-Tv-@@@-копия', 'steklopod-office-Tv-@@@-копия', 28105, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 3-8', 'Tv 3-8', 28106, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('Tv 3-5', 'Tv 3-5', 28107, NULL, NULL);
INSERT INTO database.table_keywords (label, value, id, icon, stamp)
VALUES ('ул. Тургенева', 'ул. Тургенева', 28108, NULL, NULL);