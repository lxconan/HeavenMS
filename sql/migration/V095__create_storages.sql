CREATE TABLE IF NOT EXISTS `storages`
(
    `storageid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `accountid` int(11)          NOT NULL DEFAULT '0',
    `world`     int(2)           NOT NULL,
    `slots`     int(11)          NOT NULL DEFAULT '0',
    `meso`      int(11)          NOT NULL DEFAULT '0',
    PRIMARY KEY (`storageid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
