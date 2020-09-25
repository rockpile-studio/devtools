drop index idx1_user on sys_user;
drop table if exists sys_user;
create table sys_user(
   id                   bigint not null comment '用户id',
   user_name            varchar(32) not null comment '用户名',
   password             varchar(32) not null comment '密码',
   login_name           varchar(32) not null comment '登陆名',
   telephone            varchar(16) comment '电话号码',
   regist_time          datetime not null default CURRENT_TIMESTAMP comment '注册时间',
   random_salt          varchar(16) not null comment '随机盐',
   tenant_id            bigint not null default 0 comment '租户id，默认0',
   is_disabled          tinyint not null default 0 comment '是否禁用：0否(默认) 1是',
   primary key (id)
);
alter table sys_user comment '用户';
create unique index idx1_user on sys_user(user_name);

drop index idx1_role on sys_role;
drop table if exists sys_role;
create table sys_role(
   id                   bigint not null comment '角色id',
   role_name            varchar(32) not null comment '角色名',
   description          varchar(255) comment '描述',
   is_disabled          tinyint not null default 0 comment '是否禁用：0否(默认) 1是',
   primary key (id)
);
alter table sys_role comment '角色';
create unique index idx1_role on sys_role(role_name);

drop table if exists sys_user_role;
create table sys_user_role(
   id                   bigint not null,
   user_id              bigint not null,
   role_id              bigint not null,
   is_disabled          tinyint not null default 0 comment '是否禁用：0否(默认) 1是',
   primary key (id)
);
alter table sys_user_role comment '用户角色';

drop table if exists tst_order;
create table tst_order(
   id                   bigint not null comment '订单id',
   user_id              bigint not null comment '用户id',
   goods_id             bigint not null comment '商品id',
   price                decimal(10,2) not null comment '单价',
   count                int not null comment '数量',
   address_id           bigint not null comment '收货地址id',
   state                varchar(8) not null comment '订单状态：pay已支付，delivery配送中，receive接收，cancel取消',
   created_time         datetime not null default CURRENT_TIMESTAMP comment '下单时间',
   notes                varchar(255) comment '订单备注',
   primary key (id)
);
alter table tst_order comment '订单表';

/********** 构造数据 **********/
insert into sys_user(id, user_name, password, login_name, telephone, regist_time, random_salt, tenant_id, is_disabled)
values (1309387931860901890, 'rockpile', '6cc4a866ef0c4d2ca177ae09962d80de', '林', '15959190253', '2020-09-25 02:03:05', 'DPu53d96', 0, 0);

insert into sys_role(id, role_name, description) values (50306601, 'super', '超级管理员');
insert into sys_role(id, role_name, description) values (50306602, 'admin', '管理员');
insert into sys_role(id, role_name, description) values (50306603, 'user', '平台用户');

insert into sys_user_role(id, user_id, role_id) values (1, 1309387931860901890, 50306603);

