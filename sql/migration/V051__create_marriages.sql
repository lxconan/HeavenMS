CREATE TABLE IF NOT EXISTS `marriages`
(
    `marriageid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `husbandid`  int(10) unsigned NOT NULL DEFAULT '0',
    `wifeid`     int(10) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`marriageid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
