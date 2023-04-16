DROP DATABASE IF EXISTS PlayerHeadHunt;
CREATE DATABASE IF NOT EXISTS PlayerHeadHunt;
USE PlayerHeadHunt;

 CREATE USER IF NOT EXISTS 'PlayerHeadHunt'@'%' IDENTIFIED WITH mysql_native_password BY 'PasswordPlayerHeadHunt321';
 FLUSH PRIVILEGES;
 GRANT SELECT ON PlayerHeadHunt.* TO PlayerHeadHunt@'%';
 GRANT INSERT ON PlayerHeadHunt.* TO PlayerHeadHunt@'%';
 GRANT UPDATE ON PlayerHeadHunt.* TO PlayerHeadHunt@'%';
 GRANT DELETE ON PlayerHeadHunt.* TO PlayerHeadHunt@'%';

CREATE TABLE playerdata (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  uuid VARCHAR(36),
  username VARCHAR(16),
  headsCollected int DEFAULT 0,
  INDEX players (uuid(8))
);
create index playerdata_username on playerdata (username);

CREATE TABLE heads (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  playerid INT NOT NULL DEFAULT 0,
  headcordx INT NOT NULL,
  headcordy INT NOT NULL,
  headcordz INT NOT NULL,
  FOREIGN KEY (playerid) REFERENCES playerdata (id)
);