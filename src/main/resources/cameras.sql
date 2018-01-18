CREATE FUNCTION face_control.get_cameras()
  RETURNS REFCURSOR
LANGUAGE plpgsql
AS $$
DECLARE
  ref REFCURSOR;
BEGIN
  OPEN ref FOR SELECT *
               FROM face_control.s_cameras
               ORDER BY NAME
               --                FETCH FIRST 100 ROWS ONLY
               FOR UPDATE;
  RETURN ref;
END;
$$;

-- Удалить:
DROP FUNCTION face_control.get_cameras_id_keywords();


CREATE FUNCTION face_control.get_cameras_id_keywords()
  RETURNS REFCURSOR
LANGUAGE plpgsql
AS $$
DECLARE
  ref REFCURSOR;
BEGIN
  OPEN ref FOR SELECT
                 camera,
                 name,
                 place_text,
                 face_control.s_cameras.*
               FROM face_control.s_cameras
               ORDER BY name
               FOR UPDATE;
  RETURN ref;
END;
$$;

-- Удалить:
DROP FUNCTION face_control.update_camera();

-- SELECT face_control.update_camera('Cam2', 'Cam2', 'Bname', 'C', 1, 2, 'D', 1, 1, 1, 1);

-- Обновить камеру:
CREATE OR REPLACE FUNCTION face_control.update_camera(
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
  UPDATE face_control.s_cameras
  SET
    camera       = p_id,
    name         = p_name,
    place_text   = p_place_text,
    latitude     = p_latitude,
    longitude    = p_longitude,
    note         = p_note,
    min_proc     = p_min_proc,
    territory_id = p_terr_id,
    group_id     = p_group_id,
    azimut       = p_azimut

  WHERE camera = p_old_id;
END;
$$;


-- Создать новую камеру:
CREATE OR REPLACE FUNCTION face_control.create_new_camera(
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
  INSERT INTO face_control.s_cameras
  (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
  VALUES
    (p_id, p_name, p_place_text, p_latitude, p_longitude, p_note, p_min_proc, p_terr_id, p_group_id,
     p_azimut);
END
$$
LANGUAGE plpgsql;


DROP FUNCTION face_control.delete_camera( TEXT );

--Удалить камеру:
CREATE OR REPLACE FUNCTION face_control.delete_camera(
  IN  p_id       TEXT,
  OUT is_deleted BOOLEAN
)
  RETURNS BOOLEAN AS
$$
BEGIN
  PERFORM 1
  FROM face_control.s_cameras
  WHERE p_id = camera;
  IF FOUND
  THEN
    DELETE FROM face_control.s_cameras
    WHERE p_id = camera;
    is_deleted = TRUE;
    RETURN;
  ELSE
    RAISE EXCEPTION USING MESSAGE = 'Камера с данным id не найдена.';
    RAISE NOTICE 'Проверьте правильность id.';
    is_deleted = FALSE;
    RETURN;
  END IF;
END;
$$
LANGUAGE plpgsql;


-- Создать ключевые слова (не используется)
CREATE OR REPLACE FUNCTION face_control.insert_cameras_keywords(keywords VARCHAR [])
  RETURNS VOID AS
$BODY$
DECLARE
  number_strings INTEGER := array_length(keywords, 1);
  id_index       INTEGER := 1;
BEGIN
  WHILE id_index <= number_strings LOOP
    INSERT INTO face_control.s_cameras_keywords (label, value) VALUES (keywords [id_index], keywords [id_index]);
    id_index = id_index + 1;
  END LOOP;
END;
$BODY$
LANGUAGE plpgsql;

-- Создать view

CREATE OR REPLACE VIEW face_control.cameras_by_name AS
  SELECT
    camera,
    name,
    place_text,
    azimut,
    note,
    min_proc,
    longitude,
    latitude
  FROM face_control.s_cameras
  ORDER BY name;





