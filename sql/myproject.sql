/*
 Navicat Premium Dump SQL

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : myproject

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 16/06/2026 14:46:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for distributor
-- ----------------------------
DROP TABLE IF EXISTS `distributor`;
CREATE TABLE `distributor`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分销商ID',
  `realName` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NOT NULL COMMENT '手机号',
  `parentId` bigint NULL DEFAULT NULL COMMENT '上级分销商ID（NULL表示顶级）',
  `totalSales` decimal(10, 2) NULL DEFAULT NULL COMMENT '累计销售额',
  `totalCommission` decimal(10, 2) NULL DEFAULT NULL COMMENT '累计佣金',
  `status` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NOT NULL DEFAULT '正常' COMMENT '状态',
  `createdTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatedTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `totalSales`(`totalSales` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = gb2312 COLLATE = gb2312_chinese_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of distributor
-- ----------------------------
INSERT INTO `distributor` VALUES (1, '张三', '1380000000', 0, 5000.00, 500.00, '正常', '2026-06-08 20:40:31', '2026-06-10 22:08:39');
INSERT INTO `distributor` VALUES (2, '李四', '1390000000', 0, 4000.00, 400.00, '正常', '2026-06-08 20:51:23', '2026-06-10 22:08:45');
INSERT INTO `distributor` VALUES (3, '王五', '13800003333', 0, 6000.00, 600.00, '正常', '2026-06-10 22:09:25', '2026-06-10 22:09:25');
INSERT INTO `distributor` VALUES (4, '赵六', '13800004444', 2, 400.00, 40.00, '正常', '2026-06-10 22:09:53', '2026-06-14 21:55:22');
INSERT INTO `distributor` VALUES (5, '钱七', '13800005555', 1, 500.00, 50.00, '正常', '2026-06-10 22:10:16', '2026-06-10 22:10:16');
INSERT INTO `distributor` VALUES (6, '孙八', '13800006666', 1, 600.00, 60.00, '正常', '2026-06-10 22:10:46', '2026-06-10 22:10:46');
INSERT INTO `distributor` VALUES (7, '周九', '13800007777', 2, 500.00, 50.00, '正常', '2026-06-10 22:11:03', '2026-06-10 22:11:03');
INSERT INTO `distributor` VALUES (8, '吴十', '13800008888', 2, 800.00, 80.00, '正常', '2026-06-10 22:11:34', '2026-06-10 22:11:34');
INSERT INTO `distributor` VALUES (9, '刘子', '13800009999', 2, 600.00, 60.00, '正常', '2026-06-12 14:02:38', '2026-06-12 14:02:38');
INSERT INTO `distributor` VALUES (10, '刚子', '15700000001', 3, 300.00, 30.00, '正常', '2026-06-12 14:03:12', '2026-06-12 14:03:12');
INSERT INTO `distributor` VALUES (11, '六六', '15700000002', 3, 500.00, 50.00, '正常', '2026-06-12 14:03:39', '2026-06-12 14:03:39');
INSERT INTO `distributor` VALUES (12, '七七', '15700000003', 3, 400.00, 40.00, '正常', '2026-06-12 14:16:37', '2026-06-12 14:25:28');
INSERT INTO `distributor` VALUES (13, '111', '11111111111', NULL, NULL, NULL, '正常', '2026-06-12 14:29:55', '2026-06-12 14:29:55');
INSERT INTO `distributor` VALUES (14, '222', '22222222222', NULL, NULL, NULL, '正常', '2026-06-12 14:30:15', '2026-06-12 14:30:15');
INSERT INTO `distributor` VALUES (15, '333', '33333333333', NULL, NULL, NULL, '正常', '2026-06-12 14:30:26', '2026-06-12 14:30:26');
INSERT INTO `distributor` VALUES (16, '444', '44444444444', NULL, NULL, NULL, '正常', '2026-06-12 14:30:33', '2026-06-12 14:30:33');
INSERT INTO `distributor` VALUES (17, '555', '55555555555', NULL, NULL, NULL, '正常', '2026-06-12 14:30:41', '2026-06-12 14:30:41');
INSERT INTO `distributor` VALUES (18, '666', '66666666666', NULL, NULL, NULL, '正常', '2026-06-12 14:30:50', '2026-06-12 14:30:50');
INSERT INTO `distributor` VALUES (19, '777', '77777777777', NULL, NULL, NULL, '正常', '2026-06-12 14:30:57', '2026-06-12 14:30:57');
INSERT INTO `distributor` VALUES (20, '888', '88888888888', NULL, NULL, NULL, '正常', '2026-06-12 14:31:04', '2026-06-12 14:31:04');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID（自增主键）',
  `orderNumber` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NOT NULL COMMENT '订单编号',
  `productID` int NOT NULL COMMENT '商品ID',
  `distributorID` int NOT NULL COMMENT '分销商ID',
  `orderNum` int NULL DEFAULT NULL COMMENT '订单数量',
  `totalAmount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `totalcommission` decimal(10, 2) NOT NULL COMMENT '佣金金额',
  `status` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NOT NULL DEFAULT '待发货' COMMENT '状态：1=待发货 2=待结算 3=已完成 4=已取消 5=已退款',
  `createdTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `updatedTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `distributor_id`(`distributorID` ASC) USING BTREE,
  INDEX `orders.distributor_money`(`totalAmount` ASC) USING BTREE,
  INDEX `orders.product`(`productID` ASC) USING BTREE,
  CONSTRAINT `orders.distributor_id` FOREIGN KEY (`distributorID`) REFERENCES `distributor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `orders.product` FOREIGN KEY (`productID`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = gb2312 COLLATE = gb2312_chinese_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (18, 'ORD202606142139311615', 1, 1, 2, 200.00, 20.00, '待发货', '2026-06-14 21:39:36', '2026-06-14 21:39:36');
INSERT INTO `orders` VALUES (19, 'ORD202606142142549101', 1, 1, 1, 100.00, 10.00, '待发货', '2026-06-14 21:43:01', '2026-06-14 21:43:01');
INSERT INTO `orders` VALUES (20, 'ORD202606142156283962', 1, 2, 3, 300.00, 30.00, '待发货', '2026-06-14 21:56:51', '2026-06-14 21:56:51');
INSERT INTO `orders` VALUES (21, 'ORD202606142200460886', 1, 4, 3, 300.00, 30.00, '待发货', '2026-06-14 22:00:54', '2026-06-14 22:00:54');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '商品名称',
  `category` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '分类',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '售价',
  `commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '佣金',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `sales` int NULL DEFAULT NULL COMMENT '已售数量',
  `imageUrl` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '商品图片URL',
  `status` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT '上架' COMMENT '状态',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = gb2312 COLLATE = gb2312_chinese_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '高级护肤品套装', '美妆', 100.00, 10.00, 200, 156, '/uploads/e7d6c054-0d38-44f2-8e07-d07fbbd452fb.jpg', '上架', '2026-06-08 21:09:54', '2026-06-16 14:43:33');
INSERT INTO `product` VALUES (2, '健康保健品', '保健', 520.00, 10.00, 500, 328, NULL, '上架', '2026-06-08 21:10:35', '2026-06-08 21:10:35');
INSERT INTO `product` VALUES (3, '进口红酒礼盒', '酒水', 800.00, 8.00, 150, 89, NULL, '上架', '2026-06-08 21:11:09', '2026-06-08 21:11:09');
INSERT INTO `product` VALUES (4, '有机食品礼包', '食品', 890.00, 12.00, 0, 67, NULL, '下架', '2026-06-08 21:11:45', '2026-06-08 21:11:45');
INSERT INTO `product` VALUES (5, '精品茶叶礼盒', '茶饮', 680.00, 10.00, 300, 215, NULL, '上架', '2026-06-08 21:12:19', '2026-06-08 21:12:19');
INSERT INTO `product` VALUES (6, '卤鸡堡', '食品', 888.00, 88.00, 200, 100, NULL, '上架', '2026-06-12 14:04:31', '2026-06-12 14:05:33');
INSERT INTO `product` VALUES (7, '冰激凌', '零食', 6.00, 6.00, 100, 100, NULL, '上架', '2026-06-12 14:05:28', '2026-06-12 14:05:28');
INSERT INTO `product` VALUES (8, '米饭', '食品', 10.00, 1.00, 50, 100, NULL, '上架', '2026-06-12 14:05:55', '2026-06-12 14:05:55');
INSERT INTO `product` VALUES (9, '鸡肉', '食品', 7.00, 6.00, 50, 100, NULL, '上架', '2026-06-12 14:06:23', '2026-06-12 14:06:23');
INSERT INTO `product` VALUES (10, '粽子', '食品', 8.00, 6.00, 30, 20, NULL, '上架', '2026-06-12 14:06:45', '2026-06-12 14:06:45');
INSERT INTO `product` VALUES (11, '鸭肉', '食品', 11.00, 11.00, 11, 11, NULL, '上架', '2026-06-12 14:06:53', '2026-06-12 14:07:00');
INSERT INTO `product` VALUES (12, '111', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:13:52', '2026-06-12 15:13:52');
INSERT INTO `product` VALUES (13, '222', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:13:55', '2026-06-12 15:13:55');
INSERT INTO `product` VALUES (14, '333', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:00', '2026-06-12 15:14:00');
INSERT INTO `product` VALUES (15, '444', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:04', '2026-06-12 15:14:04');
INSERT INTO `product` VALUES (16, '555', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:08', '2026-06-12 15:14:08');
INSERT INTO `product` VALUES (17, '666', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:13', '2026-06-12 15:14:13');
INSERT INTO `product` VALUES (18, '777', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:17', '2026-06-12 15:14:17');
INSERT INTO `product` VALUES (19, '888', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:21', '2026-06-12 15:14:21');
INSERT INTO `product` VALUES (20, '999', NULL, NULL, NULL, NULL, NULL, NULL, '上架', '2026-06-12 15:14:26', '2026-06-12 15:14:26');
INSERT INTO `product` VALUES (24, '666', '美妆', 0.00, 0.00, 0, 0, '', '', '2026-06-14 14:20:01', '2026-06-14 14:20:01');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '角色',
  `status` varchar(255) CHARACTER SET gb2312 COLLATE gb2312_chinese_ci NULL DEFAULT NULL COMMENT '状态',
  `lastLogin` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `createdTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatedTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = gb2312 COLLATE = gb2312_chinese_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (3, 'admin', '123456', NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-07 12:42:17', '2026-06-07 12:42:28');

SET FOREIGN_KEY_CHECKS = 1;
