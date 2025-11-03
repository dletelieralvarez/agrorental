create database securitydbjwt;

create user 'myuser'@'%' identified by 'password';

grant all on securitydbjwt.* to 'myuser'@'%';