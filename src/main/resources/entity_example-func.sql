CREATE FUNCTION database.get_tv()
  RETURNS REtvURSOR
LANGUAGE plpgsql
AS $$
DECLARE
  ref REtvURSOR;
BEGIN
  OPEN ref FOR SELECT *
               FROM database.table
               ORDER BY NAME
               --                FETCH FIRST 100 ROWS ONLY
               FOR UPDATE;
  RETURN ref;
END;
$$;

-- Удалить:
DROP FUNCTION database.get_tv_id_keywords();


CREATE FUNCTION database.get_tv_id_keywords()
  RETURNS REtvURSOR
LANGUAGE plpgsql
AS $$
DECLARE
  ref REtvURSOR;
BEGIN
  OPEN ref FOR SELECT
                 Tvera,
                 name,
                 place_text,
                 database.table.*
               FROM database.table
               ORDER BY name
               FOR UPDATE;
  RETURN ref;
END;
$$;

-- Удалить:
DROP FUNCTION database.update_Tvera();

-- SELECT database.update_Tvera('Tv2', 'Tv2', 'Bname', 'C', 1, 2, 'D', 1, 1, 1, 1);

-- Обновить камеру:
CREATE OR REPLACE FUNCTION database.update_Tvera(
  IN p_old_id     TEXT,
  IN p_id         TEXT,
  IN p_name       TEXT,
  IN p_place_text TEXT,
  IN p_latitude   NUMERIC,
  IN p_longitude  NUMERIC,
  IN p_note       TEXT,
  IN p_min_proc   NUMERIC,
  IN p_terr_id    NUMERIC,
  IN p_group_id   NUMERIC,
  IN p_azimut     NUMERIC
)
  RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM 1
  FROM database.s_Tvera_ocrug
  WHERE Tvera = p_old_id;
  IF FOUND
  THEN
    UPDATE database.table
    SET
      Tvera       = p_id,
      name         = p_name,
      place_text   = p_place_text,
      latitude     = p_latitude,
      longitude    = p_longitude,
      note         = p_note,
      min_proc     = p_min_proc,
      ocrug_id = p_terr_id,
      group_id     = p_group_id,
      azimut       = p_azimut

    WHERE Tvera = p_old_id;
    RETURN;
  ELSE
    RAISE EXCEPTION USING MESSAGE = 'Сущность с данным id не найдена.';
    RAISE NOTICE 'Проверьте правильность id.';
    RETURN;
  END IF;
END;
$$;


-- Создать новую камеру:
CREATE OR REPLACE FUNCTION database.create_new_Tvera(
  IN p_id         TEXT,
  IN p_name       TEXT,
  IN p_place_text TEXT,
  IN p_latitude   NUMERIC,
  IN p_longitude  NUMERIC,
  IN p_note       TEXT,
  IN p_min_proc   NUMERIC,
  IN p_terr_id    NUMERIC,
  IN p_group_id   NUMERIC,
  IN p_azimut     NUMERIC
)
  RETURNS VOID AS
$$
BEGIN
  INSERT INTO database.table
  (Tvera, name, place_text, latitude, longitude, note, min_proc, ocrug_id, group_id, azimut)
  VALUES
    (p_id, p_name, p_place_text, p_latitude, p_longitude, p_note, p_min_proc, p_terr_id, p_group_id,
     p_azimut);
END
$$
LANGUAGE plpgsql;


DROP FUNCTION database.delete_Tvera( TEXT );

--Удалить камеру:
CREATE OR REPLACE FUNCTION database.delete_Tvera(
  IN  p_id       TEXT,
  OUT is_deleted BOOLEAN
)
  RETURNS BOOLEAN AS
$$
BEGIN
  PERFORM 1
  FROM database.table
  WHERE p_id = Tvera;
  IF FOUND
  THEN
    DELETE FROM database.table
    WHERE p_id = Tvera;
    is_deleted = TRUE;
    RETURN;
  ELSE
    RAISE EXCEPTION USING MESSAGE = 'Сущность с данным id не найдена.';
    RAISE NOTICE 'Проверьте правильность id.';
    is_deleted = FALSE;
    RETURN;
  END IF;
END;
$$
LANGUAGE plpgsql;


-- Создать ключевые слова (не используется)
CREATE OR REPLACE FUNCTION database.insert_tv_keywords(keywords VARCHAR [])
  RETURNS VOID AS
$BODY$
DECLARE
  number_strings INTEGER := array_length(keywords, 1);
  id_index       INTEGER := 1;
BEGIN
  WHILE id_index <= number_strings LOOP
    INSERT INTO database.table_keywords (label, value) VALUES (keywords [id_index], keywords [id_index]);
    id_index = id_index + 1;
  END LOOP;
END;
$BODY$
LANGUAGE plpgsql;

-- Создать view

CREATE OR REPLACE VIEW database.tv_by_name AS
  SELECT
    Tvera,
    name,
    place_text,
    azimut,
    note,
    min_proc,
    longitude,
    latitude
  FROM database.table
  ORDER BY name;