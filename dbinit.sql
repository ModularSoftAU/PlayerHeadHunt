DROP DATABASE IF EXISTS EasterEggHunt;
CREATE DATABASE IF NOT EXISTS EasterEggHunt;
USE EasterEggHunt;

 CREATE USER IF NOT EXISTS 'EasterEggHunt'@'%' IDENTIFIED WITH mysql_native_password BY 'PasswordEasterEggHunt321';
 FLUSH PRIVILEGES;
 GRANT SELECT ON EasterEggHunt.* TO EasterEggHunt@'%';
 GRANT INSERT ON EasterEggHunt.* TO EasterEggHunt@'%';
 GRANT UPDATE ON EasterEggHunt.* TO EasterEggHunt@'%';
 GRANT DELETE ON EasterEggHunt.* TO EasterEggHunt@'%';

CREATE TABLE playerdata (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  uuid VARCHAR(36),
  username VARCHAR(16),
  eggsCollected int DEFAULT 0,
  INDEX players (uuid(8))
);
create index playerdata_username on playerdata (username);

CREATE TABLE eastereggs (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  playerid INT NOT NULL DEFAULT 0,
  eggcordx INT NOT NULL,
  eggcordy INT NOT NULL,
  eggcordz INT NOT NULL,
  FOREIGN KEY (playerid) REFERENCES playerdata (id)
);

-- Trigger that increments the eggsCollected column for the user when a new egg is added
CREATE TRIGGER eastereggs_incrementEggsColected
AFTER INSERT ON eastereggs FOR EACH ROW
	UPDATE playerdata SET eggsCollected = eggsCollected + 1 WHERE id = NEW.playerid
;