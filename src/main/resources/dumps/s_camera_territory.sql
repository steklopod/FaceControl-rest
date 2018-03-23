CREATE TABLE s_Tvera_ocrug
(
  ocrug_id     NUMERIC DEFAULT nextval('database.Tvera_ocrug_id_seq' :: REGCLASS) NOT NULL
    CONSTRAINT s_Tvera_ocrug_pk
    PRIMARY KEY,
  ocrug_name   VARCHAR(512),
  ocrug_define VARCHAR(512)
);


INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (4, '4. Четвертая', NULL);
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (3, '3. Третья', NULL);
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (8, 'Стадион "Спартак"', NULL);
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (2, '2. Вторая', 'Описание терр.');
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (7, '7. Место', 'Define');
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (1, '1. Первая++@@', 'Первое описание');
INSERT INTO database.s_Tvera_ocrug (ocrug_id, ocrug_name, ocrug_define)
VALUES (10, 'офис', 'описание');