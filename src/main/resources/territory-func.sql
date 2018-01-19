-- Удалить:
DROP FUNCTION face_control.update_territory( NUMERIC, NUMERIC, TEXT, TEXT );

SELECT face_control.update_territory( 9, 9, 'Территория', 'Define');

-- Обновить Территорию:
CREATE OR REPLACE FUNCTION face_control.update_territory(
  IN p_old_id      NUMERIC,
  IN p_terr_id     NUMERIC,
  IN p_terr_name   TEXT,
  IN p_terr_define TEXT
)
  RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM 1
  FROM face_control.s_camera_territory
  WHERE p_old_id = territory_id;
  IF FOUND
  THEN
    UPDATE face_control.s_camera_territory
    SET
      territory_id     = p_terr_id,
      territory_name   = p_terr_name,
      territory_define = p_terr_define
    WHERE territory_id = p_old_id;
    RETURN;
  ELSE
    RAISE EXCEPTION USING MESSAGE = 'Территория с данным id не найдена.';
    RAISE NOTICE 'Проверьте правильность id.';
    RETURN;
  END IF;
END;
$$;