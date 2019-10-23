CREATE TABLE IF NOT EXISTS `plife`
(
    `id`      int(11) unsigned NOT NULL AUTO_INCREMENT,
    `world`   int(11)          NOT NULL DEFAULT '-1',
    `map`     int(11)          NOT NULL DEFAULT '0',
    `life`    int(11)          NOT NULL DEFAULT '0',
    `type`    varchar(1)       NOT NULL DEFAULT 'n',
    `cy`      int(11)          NOT NULL DEFAULT '0',
    `f`       int(11)          NOT NULL DEFAULT '0',
    `fh`      int(11)          NOT NULL DEFAULT '0',
    `rx0`     int(11)          NOT NULL DEFAULT '0',
    `rx1`     int(11)          NOT NULL DEFAULT '0',
    `x`       int(11)          NOT NULL DEFAULT '0',
    `y`       int(11)          NOT NULL DEFAULT '0',
    `hide`    int(11)          NOT NULL DEFAULT '0',
    `mobtime` int(11)          NOT NULL DEFAULT '0',
    `team`    int(11)          NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
