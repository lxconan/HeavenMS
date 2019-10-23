CREATE TABLE IF NOT EXISTS `playernpcs_field`
(
    `id`     int(11)     NOT NULL AUTO_INCREMENT,
    `world`  int(11)     NOT NULL,
    `map`    int(11)     NOT NULL,
    `step`   tinyint(1)  NOT NULL DEFAULT '0',
    `podium` smallint(8) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
