CREATE TABLE IF NOT EXISTS `playernpcs_equip`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `npcid`    int(11) NOT NULL DEFAULT '0',
    `equipid`  int(11) NOT NULL,
    `type`     int(11) NOT NULL DEFAULT '0',
    `equippos` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
