CREATE TABLE IF NOT EXISTS `playerdiseases`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `charid`     int(11) NOT NULL,
    `disease`    int(11) NOT NULL,
    `mobskillid` int(11) NOT NULL,
    `mobskilllv` int(11) NOT NULL,
    `length`     int(11) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
