CREATE TABLE IF NOT EXISTS `nxcode_items`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `codeid`   int(11) NOT NULL,
    `type`     int(11) NOT NULL DEFAULT '5',
    `item`     int(11) NOT NULL DEFAULT '4000000',
    `quantity` int(11) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
