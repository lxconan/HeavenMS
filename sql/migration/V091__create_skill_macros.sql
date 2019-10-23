CREATE TABLE IF NOT EXISTS `skillmacros`
(
    `id`          int(11)    NOT NULL AUTO_INCREMENT,
    `characterid` int(11)    NOT NULL DEFAULT '0',
    `position`    tinyint(1) NOT NULL DEFAULT '0',
    `skill1`      int(11)    NOT NULL DEFAULT '0',
    `skill2`      int(11)    NOT NULL DEFAULT '0',
    `skill3`      int(11)    NOT NULL DEFAULT '0',
    `name`        varchar(13)         DEFAULT NULL,
    `shout`       tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
