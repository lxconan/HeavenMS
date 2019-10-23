CREATE TABLE IF NOT EXISTS `allianceguilds`
(
    `id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `allianceid` int(10)          NOT NULL DEFAULT '-1',
    `guildid`    int(10)          NOT NULL DEFAULT '-1',
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
