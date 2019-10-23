CREATE TABLE IF NOT EXISTS `buddies`
(
    `id`          int(11)    NOT NULL AUTO_INCREMENT,
    `characterid` int(11)    NOT NULL,
    `buddyid`     int(11)    NOT NULL,
    `pending`     tinyint(4) NOT NULL DEFAULT '0',
    `group`       varchar(17)         DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
