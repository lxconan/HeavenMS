CREATE TABLE IF NOT EXISTS `makerrewarddata`
(
    `itemid`   int(11)             NOT NULL,
    `rewardid` int(11)             NOT NULL,
    `quantity` smallint(6)         NOT NULL,
    `prob`     tinyint(3) unsigned NOT NULL DEFAULT '100',
    PRIMARY KEY (`itemid`, `rewardid`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
