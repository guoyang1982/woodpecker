INSERT INTO `t_user`(
`login_name`,`user_name`,`password`,`salt`,`user_role`,`user_status`)
SELECT 'admin','admin123','ae059ce6767f4cdab80d1b30e7fd724a','c22c68eb-c346-472c-a11c-ca155672b05f',1,1
FROM dual
WHERE NOT exists(SELECT id FROM `t_user` WHERE login_name='admin');