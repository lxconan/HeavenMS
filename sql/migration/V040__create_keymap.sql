CREATE TABLE IF NOT EXISTS `keymap`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `characterid` int(11) NOT NULL DEFAULT '0',
    `key`         int(11) NOT NULL DEFAULT '0',
    `type`        int(11) NOT NULL DEFAULT '0',
    `action`      int(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
