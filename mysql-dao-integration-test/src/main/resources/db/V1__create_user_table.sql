--
-- Implements the schema for this application's database.
--

CREATE TABLE `user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT "Unique ID of user. System generated sequential key.",
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="Registered users.";