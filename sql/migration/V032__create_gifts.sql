CREATE TABLE IF NOT EXISTS `gifts`
(
    `id`      int(10) unsigned NOT NULL AUTO_INCREMENT,
    `to`      int(11)          NOT NULL,
    `from`    varchar(13)      NOT NULL,
    `message` tinytext         NOT NULL,
    `sn`      int(10) unsigned NOT NULL,
    `ringid`  int(10)          NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
