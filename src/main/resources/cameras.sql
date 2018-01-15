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


-- Обновить камеру:
CREATE OR REPLACE FUNCTION face_control.update_camera(
  IN p_old_id    TEXT,
  IN p_id        TEXT,
  IN p_territory TEXT,
  IN p_group     TEXT,
  IN p_azimut    NUMERIC,
  IN p_note      TEXT,
  IN p_min_proc  NUMERIC,
  IN p_longitude NUMERIC,
  IN p_latitude  NUMERIC
)
  RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE face_control.s_cameras
  SET
    camera     = p_id,
    name       = p_territory,
    place_text = p_group,
    azimut     = p_azimut,
    note       = p_note,
    min_proc   = p_min_proc,
    longitude  = p_longitude,
    latitude   = p_latitude

  WHERE camera = p_old_id;
END;
$$;


-- Создать новую камеру:
CREATE OR REPLACE FUNCTION face_control.create_new_camera(
  IN p_id        TEXT,
  IN p_territory TEXT,
  IN p_group     TEXT,
  IN p_latitude  NUMERIC,
  IN p_longitude NUMERIC,
  IN p_note      TEXT,
  IN p_min_proc  NUMERIC,
  IN p_azimut    NUMERIC
)
  RETURNS VOID AS
$$
BEGIN
  INSERT INTO face_control.s_cameras
  (camera, name, place_text, latitude, longitude, note, min_proc, azimut)
  VALUES
    (p_id, p_territory, p_group, p_latitude, p_longitude, p_note, p_min_proc, p_azimut);
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





