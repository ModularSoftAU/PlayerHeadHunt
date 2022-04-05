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
  eggsCollected int
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