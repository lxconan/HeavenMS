CREATE TABLE IF NOT EXISTS `area_info`
(
    `id`     int(11)      NOT NULL AUTO_INCREMENT,
    `charid` int(11)      NOT NULL,
    `area`   int(11)      NOT NULL,
    `info`   varchar(200) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
