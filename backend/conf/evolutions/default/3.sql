-- Add missing timestamps to rajob (fix schema mismatch with Ebean model)

# --- !Ups
-- MySQL compatibility note:
-- Some MySQL/MariaDB variants do NOT support `ADD COLUMN IF NOT EXISTS`.
-- Also, in local dev you may have already added these columns manually.
-- So we conditionally add the columns using INFORMATION_SCHEMA + dynamic SQL.

SET @schema := DATABASE();

SET @has_create_time := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob'
    AND COLUMN_NAME = 'create_time'
);

SET @sql_create_time := IF(
  @has_create_time = 0,
  'ALTER TABLE rajob ADD COLUMN create_time varchar(255) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql_create_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_update_time := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob'
    AND COLUMN_NAME = 'update_time'
);

SET @sql_update_time := IF(
  @has_update_time = 0,
  'ALTER TABLE rajob ADD COLUMN update_time varchar(255) NULL',
  'SELECT 1'
);
PREPARE stmt2 FROM @sql_update_time;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

# --- !Downs
-- Down migrations are rarely used in prod, but keep them safe-ish too.
-- (DROP COLUMN IF EXISTS is not supported everywhere, so do it conditionally.)

SET @schema := DATABASE();

SET @has_create_time := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob'
    AND COLUMN_NAME = 'create_time'
);
SET @sql_drop_create := IF(
  @has_create_time = 1,
  'ALTER TABLE rajob DROP COLUMN create_time',
  'SELECT 1'
);
PREPARE stmt3 FROM @sql_drop_create;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

SET @has_update_time := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob'
    AND COLUMN_NAME = 'update_time'
);
SET @sql_drop_update := IF(
  @has_update_time = 1,
  'ALTER TABLE rajob DROP COLUMN update_time',
  'SELECT 1'
);
PREPARE stmt4 FROM @sql_drop_update;
EXECUTE stmt4;
DEALLOCATE PREPARE stmt4;


