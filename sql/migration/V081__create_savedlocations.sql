CREATE TABLE IF NOT EXISTS `savedlocations`
(
    `id`           int(11)                                                                                                                                  NOT NULL AUTO_INCREMENT,
    `characterid`  int(11)                                                                                                                                  NOT NULL,
    `locationtype` enum ('FREE_MARKET','WORLDTOUR','FLORINA','INTRO','SUNDAY_MARKET','MIRROR','EVENT','BOSSPQ','HAPPYVILLE','DEVELOPER','MONSTER_CARNIVAL') NOT NULL,
    `map`          int(11)                                                                                                                                  NOT NULL,
    `portal`       int(11)                                                                                                                                  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
