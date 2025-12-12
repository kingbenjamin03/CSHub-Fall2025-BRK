-- Add missing `status` column to rajob_application (fix schema mismatch with Ebean model)

# --- !Ups
SET @schema := DATABASE();

SET @has_status := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob_application'
    AND COLUMN_NAME = 'status'
);

SET @sql_add_status := IF(
  @has_status = 0,
  'ALTER TABLE rajob_application ADD COLUMN status varchar(255) NULL',
  'SELECT 1'
);
PREPARE stmt FROM @sql_add_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

# --- !Downs
SET @schema := DATABASE();

SET @has_status := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'rajob_application'
    AND COLUMN_NAME = 'status'
);

SET @sql_drop_status := IF(
  @has_status = 1,
  'ALTER TABLE rajob_application DROP COLUMN status',
  'SELECT 1'
);
PREPARE stmt2 FROM @sql_drop_status;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;


