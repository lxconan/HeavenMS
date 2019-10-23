CREATE TABLE IF NOT EXISTS `wishlists`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT,
    `charid` int(11) NOT NULL,
    `sn`     int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
