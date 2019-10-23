CREATE TABLE IF NOT EXISTS `makerrecipedata`
(
    `itemid`   int(11)     NOT NULL,
    `req_item` int(11)     NOT NULL,
    `count`    smallint(6) NOT NULL,
    PRIMARY KEY (`itemid`, `req_item`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
