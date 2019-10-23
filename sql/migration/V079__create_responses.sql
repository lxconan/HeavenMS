CREATE TABLE IF NOT EXISTS `responses`
(
    `chat`     text,
    `response` text,
    `id`       int(10) unsigned NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
