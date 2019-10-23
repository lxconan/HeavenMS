CREATE TABLE IF NOT EXISTS `shops`
(
    `shopid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `npcid`  int(11)          NOT NULL DEFAULT '0',
    PRIMARY KEY (`shopid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 10000000
