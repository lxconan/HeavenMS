CREATE TABLE IF NOT EXISTS `specialcashitems`
(
    `id`       int(11) NOT NULL,
    `sn`       int(11) NOT NULL,
    `modifier` int(11) NOT NULL COMMENT '1024 is add/remove',
    `info`     int(1)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
