CREATE TABLE IF NOT EXISTS `nxcoupons`
(
    `id`        int(11) NOT NULL AUTO_INCREMENT,
    `couponid`  int(11) NOT NULL DEFAULT '0',
    `rate`      int(11) NOT NULL DEFAULT '0',
    `activeday` int(11) NOT NULL DEFAULT '0',
    `starthour` int(11) NOT NULL DEFAULT '0',
    `endhour`   int(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 41
