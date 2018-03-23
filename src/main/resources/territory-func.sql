-- Удалить:
DROP FUNCTION database.update_ocrug( NUMERIC, NUMERIC, TEXT, TEXT );

SELECT database.update_ocrug( 9, 9, 'Место', 'Define');

-- Обновить Территорию:
CREATE OR REPLACE FUNCTION database.update_ocrug(
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
  FROM database.s_Tvera_ocrug
  WHERE p_old_id = ocrug_id;
  IF FOUND
  THEN
    UPDATE database.s_Tvera_ocrug
    SET
      ocrug_id     = p_terr_id,
      ocrug_name   = p_terr_name,
      ocrug_define = p_terr_define
    WHERE ocrug_id = p_old_id;
    RETURN;
  ELSE
    RAISE EXCEPTION USING MESSAGE = 'Место с данным id не найдена.';
    RAISE NOTICE 'Проверьте правильность id.';
    RETURN;
  END IF;
END;
$$;