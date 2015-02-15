# JavaMailer
Send mail to different Email accounts

First you need to setup database by following commands:
# Create database:
CREATE DATABSE database_name

# Create table
CREATE TABLE `EmailQueue` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `from_email_address` varchar(5000) DEFAULT NULL,
 `to_email_address` varchar(5000) DEFAULT NULL,
 `subject` varchar(1024) DEFAULT NULL,
 `body` text,
 `status` int(10) DEFAULT '0',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1

# Insert some enteries
