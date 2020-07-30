drop table if exists inventory_entry;
drop table if exists item;
drop table if exists survivor;

drop type if exists gender;
create type gender as enum('MALE', 'FEMALE', 'OTHER');

create table survivor(
	id bigserial primary key,
  name varchar(50) not null,
  age smallint not null,
  gender gender not null,
  latitude double precision not null,
  longitude double precision not null  
);

create table item(
	id bigserial primary key,
  name varchar(50) not null,
  points int not null
);

create table inventory_entry(
	survivor_id bigint not null,
  item_id bigint not null,
  amount smallint not null,
  
  primary key(survivor_id, item_id),
  foreign key(survivor_id) references survivor(id)
  	on delete cascade on update cascade,
  foreign key(item_id) references item(id)
  	on delete cascade on update cascade
);

create table report(
	reporter_id bigint not null,
  reported_id bigint not null,

  primary key(reporter_id, reported_id),
  foreign key(reporter_id) references survivor(id)
  	on delete cascade on update cascade,
  foreign key(reported_id) references survivor(id)
  	on delete cascade on update cascade
);

insert into item(name, points) values 
('Fiji Water', 14), 
('Campbell Soup', 12),
('First Aid Pouch', 10), 
('AK47', 8);