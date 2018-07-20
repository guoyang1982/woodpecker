CREATE TABLE IF NOT EXISTS `t_user`(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `login_name` VARCHAR(250) NOT NULL ,
  `user_name` VARCHAR(250) NULL ,
  `password` VARCHAR(250) NOT NULL ,
  `salt` VARCHAR(100)  NULL ,
  `user_role` TINYINT  NULL ,
  `mobile_phone` VARCHAR(200) NULL ,
  `mail` VARCHAR(200) NULL ,
  `user_status` TINYINT NOT NULL ,
  `department` VARCHAR(250)  NULL ,
  `create_time` DATETIME  NULL ,
  `modify_time` DATETIME  NULL,
  PRIMARY KEY(`login_name`)
);


CREATE TABLE IF NOT EXISTS `t_user_app`
(
  `id`         INTEGER AUTO_INCREMENT NOT NULL,
  `user_id`    INTEGER   NOT NULL,
  `app_id`     VARCHAR(250)   NOT NULL,
  `status`     TINYINT DEFAULT '1'   NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
  PRIMARY KEY (`user_id`,`app_id`)
);
