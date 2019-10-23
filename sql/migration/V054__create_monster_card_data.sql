CREATE TABLE IF NOT EXISTS `monstercarddata`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT,
    `cardid` int(11) NOT NULL DEFAULT '0',
    `mobid`  int(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 309
