CREATE TABLE IF NOT EXISTS `macbans`
(
    `macbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `mac`      varchar(30)      NOT NULL,
    `aid`      varchar(40) DEFAULT NULL,
    PRIMARY KEY (`macbanid`),
    UNIQUE KEY `mac_2` (`mac`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
