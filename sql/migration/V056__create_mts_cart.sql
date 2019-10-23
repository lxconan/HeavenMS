CREATE TABLE IF NOT EXISTS `mts_cart`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT,
    `cid`    int(11) NOT NULL,
    `itemid` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
