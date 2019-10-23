CREATE TABLE IF NOT EXISTS `rings`
(
    `id`            int(11)      NOT NULL AUTO_INCREMENT,
    `partnerRingId` int(11)      NOT NULL DEFAULT '0',
    `partnerChrId`  int(11)      NOT NULL DEFAULT '0',
    `itemid`        int(11)      NOT NULL DEFAULT '0',
    `partnername`   varchar(255) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
