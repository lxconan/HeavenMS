CREATE TABLE IF NOT EXISTS `monsterbook`
(
    `charid` int(11) unsigned NOT NULL,
    `cardid` int(11)          NOT NULL,
    `level`  int(1) DEFAULT '1'
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
