CREATE TABLE IF NOT EXISTS `reactordrops`
(
    `reactordropid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `reactorid`     int(11)          NOT NULL,
    `itemid`        int(11)          NOT NULL,
    `chance`        int(11)          NOT NULL,
    `questid`       int(5)           NOT NULL DEFAULT '-1',
    PRIMARY KEY (`reactordropid`),
    KEY `reactorid` (`reactorid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  PACK_KEYS = 1
  AUTO_INCREMENT = 841
