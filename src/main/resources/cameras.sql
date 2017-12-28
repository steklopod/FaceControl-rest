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
  OPEN ref FOR SELECT camera, name, place_text, face_control.s_cameras.*
               FROM face_control.s_cameras
               ORDER BY name
               FOR UPDATE;
  RETURN ref;
END;
$$;





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



CREATE OR REPLACE FUNCTION face_control.insert_cameras_keywords(keywords VARCHAR [])
  RETURNS VOID AS
$BODY$
DECLARE
  number_strings INTEGER := array_length(keywords, 1);
  id_index   INTEGER := 1;
BEGIN
  WHILE id_index <= number_strings LOOP
    INSERT INTO face_control.s_cameras_keywords (label, value)  VALUES (keywords[id_index], keywords[id_index]);
    id_index = id_index + 1;
  END LOOP;
END;
$BODY$
LANGUAGE plpgsql;



