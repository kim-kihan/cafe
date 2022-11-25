drop database if exists ssafy_mobile_cafe;
select @@global.transaction_isolation, @@transaction_isolation;
set @@transaction_isolation="read-committed";

create database ssafy_mobile_cafe;
use ssafy_mobile_cafe;

create table t_user(
	id varchar(100) primary key,
    name varchar(100) not null,
    pass varchar(100) not null,
    stamps integer default 0,
    age varchar(100) not null,
    gender varchar(100) not null
);
create table t_product(
	id integer auto_increment primary key,
    name varchar(100) not null,
    type varchar(20) not null,
    price integer not null,
    img varchar(100) not null,
    date timestamp default CURRENT_TIMESTAMP,
    views integer not null default 0
);


create  table t_order(
	o_id integer auto_increment primary key,
    user_id varchar(100) not null,
    order_table varchar(20),
    order_time timestamp default CURRENT_TIMESTAMP,    
    completed char(1) default 'N',
    constraint fk_order_user foreign key (user_id) references t_user(id) on delete cascade
);

create table t_order_detail(
	d_id integer auto_increment primary key,
    order_id integer not null,
    product_id integer not null,
    quantity integer not null default 1,
    constraint fk_order_detail_product foreign key (product_id) references t_product(id) on delete cascade,
    constraint fk_order_detail_order foreign key(order_id) references t_order(o_id) on delete cascade
);                                                 

create table t_stamp(
	id integer auto_increment primary key,
    user_id varchar(100) not null,
    order_id integer not null,
    quantity integer not null default 1,
    constraint fk_stamp_user foreign key (user_id) references t_user(id) on delete cascade,
    constraint fk_stamp_order foreign key (order_id) references t_order(o_id) on delete cascade
);

create table t_search(
	id integer auto_increment primary key,
    user_id varchar(100) not null,
    content varchar(100) not null,
    constraint fk_search_user foreign key (user_id) references t_user(id) on delete cascade
);

create table t_comment(
	id integer auto_increment primary key,
    user_id varchar(100) not null,
    product_id integer not null,
    rating float not null default 1,
    comment varchar(200),
    constraint fk_comment_user foreign key(user_id) references t_user(id) on delete cascade,
    constraint fk_comment_product foreign key(product_id) references t_product(id) on delete cascade
);


INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy01', '김싸피', 'pass01', 5,'1997-01-02', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy02', '황원태', 'pass02', 0,'1984-04-12', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy03', '한정일', 'pass03', 3,'1998-08-22', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy04', '반장운', 'pass04', 4,'2002-08-30', 'w');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy05', '박하윤', 'pass05', 5,'1977-07-14', 'w');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy06', '정비선', 'pass06', 6,'1994-04-16', 'w');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy07', '김병관', 'pass07', 7,'1978-05-27', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy08', '강석우', 'pass08', 8,'1988-12-22', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy09', '견본무', 'pass09', 9,'2004-11-09', 'm');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('ssafy10', '전인성', 'pass10', 20,'1976-10-06', 'w');
INSERT INTO t_user (id, name, pass, stamps,age, gender) VALUES ('q', 'q', 'q', 0,'2008-10-17', 'w');

INSERT INTO t_product (name, type, price, img, date, views) VALUES ('아메리카노', 'coffee', 4100, 'coffee1.png','2022-05-21 13:59:52' , 423);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('카페라떼', 'coffee', 4500, 'coffee2.png','2022-05-27 13:59:52' , 114);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('카라멜 마끼아또', 'coffee', 4800, 'coffee3.png','2022-05-26 13:59:52' , 342);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('카푸치노', 'coffee', 4800, 'coffee4.png','2022-04-25 13:59:52' ,523);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('모카라떼', 'coffee', 4800, 'coffee5.png','2022-03-25 13:59:52' ,222);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('민트라떼', 'coffee', 4300, 'coffee6.png','2022-05-`5 13:59:52' ,224);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('화이트 모카라떼', 'coffee', 4800, 'coffee7.png','2022-05-02 13:59:52' ,223);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('자몽에이드', 'coffee', 5100, 'coffee8.png','2022-03-15 13:59:52' ,523);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('레몬에이드', 'coffee', 5100, 'coffee9.png','2022-04-05 13:59:52' ,444);
INSERT INTO t_product (name, type, price, img, date, views) VALUES ('초코칩 쿠키', 'cookie', 1500, 'cookie.png','2022-04-11 13:59:52' ,79);
commit;
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy01', 1, 1, '신맛 강한 커피는 좀 별루네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy02', 1, 2, '커피 맛을 좀 신경 써야 할 것 같네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy03', 1, 3, '그냥 저냥');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy04', 4, 4, '갠춘한 맛');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy05', 5, 5, 'SoSSo');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy06', 6, 6, '그냥 저냥 먹을만함');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy07', 7, 10, '이집 화이트 모카라떼가 젤 나은듯');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy08', 8, 8, '자몽 특유의 맛이 살아있네요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy09', 8, 9, '수제 자몽에이드라 그런지 맛나요.');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('ssafy10', 10, 10, '초코칩 쿠키 먹으로 여기 옵니다.');

INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy01', 'order_table 01', '2022-05-21 13:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy01', 'order_table 02', '2022-05-21 15:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy03', 'order_table 03', '2022-05-21 12:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy04', 'order_table 04', '2022-05-21 15:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy05', 'order_table 05', '2022-05-21 18:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy06', 'order_table 06', '2022-05-21 15:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy07', 'order_table 07', '2022-05-21 10:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy08', 'order_table 08', '2022-05-21 15:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy09', 'order_table 09', '2022-05-21 15:59:52');
INSERT INTO t_order (user_id, order_table, order_time) VALUES ('ssafy10', 'order_table 10', '2022-05-21 17:59:52');

INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (1, 1, 1);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (1, 2, 3);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (2, 8, 1);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (3, 3, 3);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (4, 4, 4);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (5, 5, 5);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (6, 6, 6);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (7, 7, 7);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (8, 8, 8);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (9, 9, 9);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (10, 8, 10);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (10, 10, 10);


INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy01', 1, 4);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy01', 2, 1);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy03', 3, 3);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy04', 4, 4);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy05', 5, 5);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy06', 6, 6);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy07', 7, 7);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy08', 8, 8);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy09', 9, 9);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('ssafy10', 10, 20);

INSERT INTO t_search (user_id, content) VALUES ('ssafy01', '아메리카노');
INSERT INTO t_search (user_id, content) VALUES ('ssafy01', '카푸치노');
INSERT INTO t_search (user_id, content) VALUES ('ssafy01', '쿠키');
INSERT INTO t_search (user_id, content) VALUES ('ssafy01', '라떼');

commit;

