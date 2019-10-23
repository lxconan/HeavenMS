CREATE TABLE IF NOT EXISTS `petignores`
(
    `id`     int(11) unsigned NOT NULL AUTO_INCREMENT,
    `petid`  int(11) unsigned NOT NULL,
    `itemid` int(10) unsigned NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_petignorepetid` FOREIGN KEY (`petid`) REFERENCES `pets` (`petid`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
