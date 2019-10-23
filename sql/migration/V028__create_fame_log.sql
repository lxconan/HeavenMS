CREATE TABLE IF NOT EXISTS `famelog`
(
    `famelogid`      int(11)   NOT NULL AUTO_INCREMENT,
    `characterid`    int(11)   NOT NULL DEFAULT '0',
    `characterid_to` int(11)   NOT NULL DEFAULT '0',
    `when`           timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`famelogid`),
    KEY `characterid` (`characterid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
