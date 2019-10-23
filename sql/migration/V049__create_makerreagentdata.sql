CREATE TABLE IF NOT EXISTS `makerreagentdata`
(
    `itemid` int(11)     NOT NULL,
    `stat`   varchar(20) NOT NULL,
    `value`  smallint(6) NOT NULL,
    PRIMARY KEY (`itemid`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
