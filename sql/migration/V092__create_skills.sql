CREATE TABLE IF NOT EXISTS `skills`
(
    `id`          int(11)    NOT NULL AUTO_INCREMENT,
    `skillid`     int(11)    NOT NULL DEFAULT '0',
    `characterid` int(11)    NOT NULL DEFAULT '0',
    `skilllevel`  int(11)    NOT NULL DEFAULT '0',
    `masterlevel` int(11)    NOT NULL DEFAULT '0',
    `expiration`  bigint(20) NOT NULL DEFAULT '-1',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `skillpair` (`skillid`, `characterid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
