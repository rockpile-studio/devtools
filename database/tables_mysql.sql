drop index idx1_user on sys_user;
drop table if exists sys_user;
create table sys_user
(
   id                   bigint not null comment '用户id',
   user_name            varchar(32) not null comment '用户名',
   password             varchar(32) not null comment '密码',
   login_name           varchar(32) not null comment '登陆名',
   telephone            varchar(16) comment '电话号码',
   regist_time          datetime not null default CURRENT_TIMESTAMP comment '注册时间',
   random_salt          varchar(16) not null comment '随机盐',
   tenant_id            bigint not null default 0 comment '租户id，默认0',
   primary key (id)
);

alter table sys_user comment '用户';
create unique index idx1_user on sys_user( user_name );

insert into sys_user(id, user_name, password, login_name, telephone, tenant_id)
values (1309042228009664514, 'rockpile', '5fb0aa535df6f5c11380ccaee87bd84f', '林', '15959190253', 0);
