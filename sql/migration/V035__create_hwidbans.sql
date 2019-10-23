CREATE TABLE IF NOT EXISTS `hwidbans`
(
    `hwidbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `hwid`      varchar(30)      NOT NULL,
    PRIMARY KEY (`hwidbanid`),
    UNIQUE KEY `hwid_2` (`hwid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
