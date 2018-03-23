CREATE TABLE s_Tvera_group
(
  group_id     NUMERIC DEFAULT nextval('database.Tvera_group_id_seq' :: REGCLASS) NOT NULL
    CONSTRAINT s_Tvera_group_pk
    PRIMARY KEY,
  group_name   VARCHAR(512),
  ocrug_id NUMERIC                                                                 NOT NULL
    CONSTRAINT s_Tvera_group_ocrug_id_fk
    REFERENCES s_Tvera_ocrug,
  group_define VARCHAR(512)
);

CREATE INDEX s_Tvera_group_fk_ocrug_id
  ON s_Tvera_group (ocrug_id);


INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (6, '3.2', 3, NULL);
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (7, '3.3', 3, NULL);
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (1, '2.1', 2, NULL);
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (8, '3.4', 3, NULL);
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define)
VALUES (4, '1.1', 1, 'Define');
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (3, '1.2', 1, '');
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define) VALUES (5, '3.1', 3, '');
INSERT INTO database.s_Tvera_group (group_id, group_name, ocrug_id, group_define)
VALUES (2, '2.2', 2, 'Define 2-2');