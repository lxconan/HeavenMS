CREATE TABLE IF NOT EXISTS `macfilters`
(
    `macfilterid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `filter`      varchar(30)      NOT NULL,
    PRIMARY KEY (`macfilterid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
