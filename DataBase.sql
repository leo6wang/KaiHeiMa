/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50636
Source Host           : localhost:3306
Source Database       : woliao

Target Server Type    : MYSQL
Target Server Version : 50636
File Encoding         : 65001

Date: 2017-05-29 17:08:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `selfId` int(11) NOT NULL,
  `friendId` int(11) NOT NULL,
  PRIMARY KEY (`selfId`,`friendId`),
  KEY `friendId` (`friendId`),
  CONSTRAINT `friendId` FOREIGN KEY (`friendId`) REFERENCES `user` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `selfId` FOREIGN KEY (`selfId`) REFERENCES `user` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES ('2008082415', '2008082414');
INSERT INTO `friend` VALUES ('2008082416', '2008082414');
INSERT INTO `friend` VALUES ('2008082418', '2008082414');
INSERT INTO `friend` VALUES ('2008082419', '2008082414');
INSERT INTO `friend` VALUES ('2008082428', '2008082414');
INSERT INTO `friend` VALUES ('2008082433', '2008082414');
INSERT INTO `friend` VALUES ('2008082414', '2008082415');
INSERT INTO `friend` VALUES ('2008082414', '2008082416');
INSERT INTO `friend` VALUES ('2008082414', '2008082418');
INSERT INTO `friend` VALUES ('2008082414', '2008082419');
INSERT INTO `friend` VALUES ('2008082430', '2008082429');
INSERT INTO `friend` VALUES ('2008082429', '2008082430');
INSERT INTO `friend` VALUES ('2008082434', '2008082430');
INSERT INTO `friend` VALUES ('2008082432', '2008082431');
INSERT INTO `friend` VALUES ('2008082433', '2008082431');
INSERT INTO `friend` VALUES ('2008082431', '2008082432');
INSERT INTO `friend` VALUES ('2008082438', '2008082432');
INSERT INTO `friend` VALUES ('2008082414', '2008082433');
INSERT INTO `friend` VALUES ('2008082431', '2008082433');
INSERT INTO `friend` VALUES ('2008082435', '2008082433');
INSERT INTO `friend` VALUES ('2008082437', '2008082433');
INSERT INTO `friend` VALUES ('2008082430', '2008082434');
INSERT INTO `friend` VALUES ('2008082433', '2008082435');
INSERT INTO `friend` VALUES ('2008082433', '2008082437');
INSERT INTO `friend` VALUES ('2008082432', '2008082438');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `sendId` int(10) NOT NULL,
  `receiveId` int(10) NOT NULL,
  `type` int(11) NOT NULL,
  `time` varchar(20) NOT NULL,
  `content` varchar(100) NOT NULL,
  PRIMARY KEY (`sendId`,`receiveId`,`time`),
  KEY `receiveId` (`receiveId`),
  CONSTRAINT `receiveId` FOREIGN KEY (`receiveId`) REFERENCES `user` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `sendId` FOREIGN KEY (`sendId`) REFERENCES `user` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES ('2008082414', '2008082418', '0', '170521143758', 'Nihao');
INSERT INTO `message` VALUES ('2008082414', '2008082419', '0', '170529043957', 'Hello');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `newstitle` varchar(255) NOT NULL,
  `newsimage` varchar(100) DEFAULT NULL,
  `newscontent` varchar(10000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES ('S6全球总决赛冠军诞生：SKT苦战五盘成就三冠王', 'E:\\NewsImage\\item_image1.jpg', '    《LOL》S6全球总决赛冠军赛今日早7点正式开打，目前比赛已经正式结束，SKT再度拿下冠军。\r\n    比赛文字简述：\r\n　　对战首局两队旗鼓相当，打了整整一个小时。在比赛进行到60分钟时双方爆发团战，两边C位全部被秒，SKT的打野奥拉夫和上单巨魔直接选择拆家，SSG残存队员实在打不动这两坨老肉，只能看着自己家被拆掉。\r\n　　第二局开场SKT微微劣势，Faker的瑞兹被SSG蛇女压了十几刀，然而Faker在10分钟后尽显魔王本色，把瑞兹玩成卡牌带活全场，SSG安掌门的千珏则有点梦游，最终SKT稳稳拿下第二局。\r\n　　第三局SSG开场就有崩盘之势，25分钟已被SKT拿下7个人头，不过SKT的ADC Bang过于膨胀送了两次人头，导致SSG打出两波反打几乎扳平劣势，整场比赛非常焦灼，打到70分钟左右，SSG靠着大龙BUFF用炮车海拿下一城！赛后数据显示，仅差10秒本场比赛就打破了由OMG和FNC保持的单场比赛时长纪录，双方ADC的输出更是双双突破了10万大关。\r\n 　 第四局SSG依然保持强力攻势，虽然双方经济差距不大，但是SSG凯南的强开团让对方防不胜防，Faker几度被秒，最终SSG拿下第四局！比赛现场也放起了“BO5战歌”，观众集体沸腾。\r\n　　第五局SKT又找回了前两局的状态，一举拿下冠军，恭喜SKT，恭喜Faker！SKT T1如今已是第三次拿下比赛冠军。');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `pwd` varchar(32) CHARACTER SET gbk NOT NULL,
  `nickName` varchar(20) CHARACTER SET gbk NOT NULL,
  `sex` char(2) CHARACTER SET gbk NOT NULL,
  `head` varchar(100) CHARACTER SET gbk DEFAULT NULL,
  `modifyTime` longtext CHARACTER SET gbk,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2008082440 DEFAULT CHARSET=gb2312;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('2008082414', '81dc9bdb52d04dc20036dbd8313ed055', 'mike', '男', '', '1338308649000');
INSERT INTO `user` VALUES ('2008082415', '81dc9bdb52d04dc20036dbd8313ed055', 'nike', '男', '', '1337774098000');
INSERT INTO `user` VALUES ('2008082416', '81dc9bdb52d04dc20036dbd8313ed055', 'Jime', '女', '', '1338042623000');
INSERT INTO `user` VALUES ('2008082417', '81dc9bdb52d04dc20036dbd8313ed055', 'hello', '女', '', null);
INSERT INTO `user` VALUES ('2008082418', '81dc9bdb52d04dc20036dbd8313ed055', 'haier', '男', '', '1338035598000');
INSERT INTO `user` VALUES ('2008082419', '81dc9bdb52d04dc20036dbd8313ed055', 'maidi', '男', '', '1338036085000');
INSERT INTO `user` VALUES ('2008082420', '81dc9bdb52d04dc20036dbd8313ed055', 'lili', '男', '', null);
INSERT INTO `user` VALUES ('2008082421', '81dc9bdb52d04dc20036dbd8313ed055', 'bier', '女', '', '1');
INSERT INTO `user` VALUES ('2008082422', '81dc9bdb52d04dc20036dbd8313ed055', 'dlnu', '男', '', '1');
INSERT INTO `user` VALUES ('2008082423', '81dc9bdb52d04dc20036dbd8313ed055', 'ruanjian', '女', '', '1');
INSERT INTO `user` VALUES ('2008082424', '81dc9bdb52d04dc20036dbd8313ed055', '3g', '男', '', '1');
INSERT INTO `user` VALUES ('2008082425', '81dc9bdb52d04dc20036dbd8313ed055', 'b2b', '女', '', '1');
INSERT INTO `user` VALUES ('2008082428', '81dc9bdb52d04dc20036dbd8313ed055', 'suohai', '男', '', '1');
INSERT INTO `user` VALUES ('2008082429', 'e10adc3949ba59abbe56e057f20f883e', 'qweqwew', '男', '', '1');
INSERT INTO `user` VALUES ('2008082430', 'e10adc3949ba59abbe56e057f20f883e', 'test1', '男', '', '1');
INSERT INTO `user` VALUES ('2008082431', 'e10adc3949ba59abbe56e057f20f883e', 'nada', '男', 'D:\\woliao\\Head\\2008082431.jpg', '1495886768000');
INSERT INTO `user` VALUES ('2008082432', 'e10adc3949ba59abbe56e057f20f883e', 'dsadsa', '男', '', '1');
INSERT INTO `user` VALUES ('2008082433', 'e10adc3949ba59abbe56e057f20f883e', 'luououou', '男', 'D:\\woliao\\Head\\2008082433.jpg', '1495887010000');
INSERT INTO `user` VALUES ('2008082434', 'e10adc3949ba59abbe56e057f20f883e', 'wqer', '男', '', '1');
INSERT INTO `user` VALUES ('2008082435', 'e10adc3949ba59abbe56e057f20f883e', 'qwew1', '男', '', '1');
INSERT INTO `user` VALUES ('2008082436', 'e10adc3949ba59abbe56e057f20f883e', 'sadasd', '男', '', '1');
INSERT INTO `user` VALUES ('2008082437', 'e10adc3949ba59abbe56e057f20f883e', 'XiaoMing', '男', '', '1');
INSERT INTO `user` VALUES ('2008082438', 'e10adc3949ba59abbe56e057f20f883e', 'XiaoHei', '男', '', '1');
INSERT INTO `user` VALUES ('2008082439', 'e10adc3949ba59abbe56e057f20f883e', 'laldala', '男', '', '1');
