CREATE TABLE IF NOT EXISTS `family_character`
(
    `cid`             int(11)    NOT NULL,
    `familyid`        int(11)    NOT NULL,
    `seniorid`        int(11)    NOT NULL,
    `reputation`      int(11)    NOT NULL DEFAULT '0',
    `todaysrep`       int(11)    NOT NULL DEFAULT '0',
    `totalreputation` int(11)    NOT NULL DEFAULT '0',
    `reptosenior`     int(11)    NOT NULL DEFAULT '0',
    `precepts`        varchar(200)        DEFAULT NULL,
    `lastresettime`   BIGINT(20) NOT NULL DEFAULT '0',
    PRIMARY KEY (`cid`),
    INDEX (cid, familyid)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
