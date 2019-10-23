CREATE TABLE IF NOT EXISTS `medalmaps`
(
    `id`            int(11)          NOT NULL AUTO_INCREMENT,
    `characterid`   int(11)          NOT NULL,
    `queststatusid` int(11) unsigned NOT NULL,
    `mapid`         int(11)          NOT NULL,
    PRIMARY KEY (`id`),
    KEY `queststatusid` (`queststatusid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
