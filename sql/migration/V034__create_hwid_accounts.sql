CREATE TABLE IF NOT EXISTS `hwidaccounts`
(
    `accountid` int(11)     NOT NULL DEFAULT '0',
    `hwid`      varchar(40) NOT NULL DEFAULT '',
    `relevance` tinyint(2)  NOT NULL DEFAULT '0',
    `expiresat` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`accountid`, `hwid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
