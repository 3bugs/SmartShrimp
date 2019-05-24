-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 24, 2019 at 08:19 AM
-- Server version: 5.7.25
-- PHP Version: 5.6.40-0+deb8u2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `smart_shrimp`
--

-- --------------------------------------------------------

--
-- Table structure for table `cycle`
--

CREATE TABLE `cycle` (
  `id` int(11) NOT NULL,
  `pond_id` int(11) NOT NULL COMMENT 'เลข id บ่อ (บอกให้รู้ว่าเป็นรอบการเลี้ยงของบ่อไหน)',
  `start_date` datetime NOT NULL,
  `initial_shrimp_count` int(11) NOT NULL COMMENT 'จำนวนกุ้งที่ปล่อย (เริ่มเลี้ยง)',
  `breed_source_id` int(11) NOT NULL COMMENT 'เลข id แหล่งพันธุ์ลูกกุ้ง',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `farm`
--

CREATE TABLE `farm` (
  `id` int(11) NOT NULL,
  `name` varchar(200) CHARACTER SET tis620 NOT NULL,
  `address` text NOT NULL,
  `sub_district` varchar(200) CHARACTER SET tis620 NOT NULL,
  `district` varchar(200) CHARACTER SET tis620 NOT NULL,
  `province` varchar(200) CHARACTER SET tis620 NOT NULL,
  `postal_code` varchar(10) NOT NULL,
  `farm_reg_id` varchar(200) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `farm`
--

INSERT INTO `farm` (`id`, `name`, `address`, `sub_district`, `district`, `province`, `postal_code`, `farm_reg_id`, `created_at`) VALUES
(2, 'Smart Shrimp', '406/1', 'ปากพะยูน', 'ปากพะยูน', 'พัทลุง', '93120', '9301032512', '2019-05-11 02:58:36');

-- --------------------------------------------------------

--
-- Table structure for table `feeding`
--

CREATE TABLE `feeding` (
  `id` int(11) NOT NULL,
  `pond_id` int(11) NOT NULL,
  `feed_date` date NOT NULL,
  `first_feed` int(11) DEFAULT NULL,
  `second_feed` int(11) DEFAULT NULL,
  `third_feed` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `feeding`
--

INSERT INTO `feeding` (`id`, `pond_id`, `feed_date`, `first_feed`, `second_feed`, `third_feed`, `created_at`) VALUES
(1, 9, '2019-05-11', 40, 60, 40, '2019-05-11 14:49:03'),
(2, 9, '2019-05-10', 750, 75, 80, '2019-05-11 14:49:03'),
(3, 9, '2019-05-09', 75, 75, 75, '2019-05-12 01:28:42'),
(4, 9, '2019-05-08', 60, 60, 60, '2019-05-12 01:28:42'),
(5, 9, '2019-05-07', 50, 50, 50, '2019-05-12 01:29:16'),
(6, 9, '2019-05-06', 50, 50, 50, '2019-05-12 01:29:49'),
(7, 9, '2019-05-05', 45, 45, 45, '2019-05-12 04:20:20'),
(8, 9, '2019-05-04', 40, 40, 40, '2019-05-12 04:38:44'),
(9, 9, '2019-05-12', 80, 45, 0, '2019-05-12 05:20:25'),
(10, 9, '2019-05-13', 50, 90, 50, '2019-05-12 05:22:01'),
(11, 9, '2019-05-14', 50, 60, 70, '2019-05-18 16:37:06'),
(12, 9, '2019-05-15', 66, 77, 88, '2019-05-18 16:37:38'),
(13, 9, '2019-05-16', 70, 80, 95, '2019-05-18 16:38:07'),
(14, 18, '2019-05-22', 10, 25, 30, '2019-05-22 03:56:50'),
(15, 18, '2019-05-21', 15, 15, 15, '2019-05-22 04:01:02'),
(16, 18, '2019-05-20', 25, 25, 25, '2019-05-22 04:01:52'),
(17, 18, '2019-05-19', 22, 19, 19, '2019-05-22 04:08:04'),
(18, 3, '2019-05-23', 1, 1, 1, '2019-05-23 13:46:05');

-- --------------------------------------------------------

--
-- Table structure for table `hatchery`
--

CREATE TABLE `hatchery` (
  `id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `address` text CHARACTER SET utf8 NOT NULL,
  `sub_district` varchar(200) NOT NULL,
  `district` varchar(200) NOT NULL,
  `province` varchar(200) NOT NULL,
  `postal_code` varchar(10) CHARACTER SET utf8 NOT NULL,
  `owner` varchar(200) NOT NULL,
  `fmd_no` varchar(200) CHARACTER SET utf8 NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=tis620;

-- --------------------------------------------------------

--
-- Table structure for table `pond`
--

CREATE TABLE `pond` (
  `id` int(11) NOT NULL,
  `farm_id` int(11) NOT NULL COMMENT 'เลข ID ฟาร์ม (บอกให้รู้ว่าเป็นบ่อของฟาร์มไหน)',
  `number` int(11) NOT NULL COMMENT 'หมายเลขบ่อ (บ่อที่)',
  `area` int(11) NOT NULL COMMENT 'พื้นที่บ่อ (ไร่)',
  `initial_shrimp_count` int(11) NOT NULL COMMENT 'จำนวนกุ้งที่ปล่อย',
  `initial_weight` int(11) DEFAULT NULL COMMENT 'น้ำหนักกุ้งเริ่มต้น',
  `final_weight` int(11) DEFAULT '-1' COMMENT 'น้ำหนักกุ้งตอนจับ (ผลผลิต)',
  `cost` int(11) DEFAULT '-1' COMMENT 'ค่าใช้จ่าย',
  `sale_price` int(11) DEFAULT '-1' COMMENT 'ราคากุ้งที่ขายได้',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pond`
--

INSERT INTO `pond` (`id`, `farm_id`, `number`, `area`, `initial_shrimp_count`, `initial_weight`, `final_weight`, `cost`, `sale_price`, `created_at`) VALUES
(3, 0, 3, 6, 800000, 0, -1, -1, -1, '2019-05-10 01:13:04'),
(4, 0, 5, 3, 150000, 0, -1, -1, -1, '2019-05-10 13:11:14'),
(8, 0, 4, 2, 100000, 0, -1, -1, -1, '2019-05-11 02:36:24'),
(9, 0, 1, 4, 500000, 0, 200, 100000, 150000, '2019-05-11 06:31:16'),
(10, 0, 6, 9, 900000, 0, -1, -1, -1, '2019-05-11 06:31:21'),
(11, 0, 7, 2, 100000, 0, -1, -1, -1, '2019-05-11 06:31:44'),
(12, 0, 8, 2, 100000, 0, -1, -1, -1, '2019-05-11 06:31:48'),
(13, 0, 9, 3, 150000, 0, -1, -1, -1, '2019-05-11 06:31:52'),
(14, 0, 10, 4, 600000, 0, -1, -1, -1, '2019-05-11 06:31:59'),
(15, 0, 11, 4, 400000, 0, -1, -1, -1, '2019-05-11 06:32:05'),
(16, 0, 12, 4, 450000, 0, -1, -1, -1, '2019-05-11 06:32:10'),
(18, 0, 2, 4, 200000, 0, 500, 40000, 50000, '2019-05-12 05:24:56');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(200) NOT NULL,
  `first_name` varchar(200) CHARACTER SET tis620 NOT NULL,
  `last_name` varchar(200) CHARACTER SET tis620 NOT NULL,
  `address` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `address`, `created_at`) VALUES
(1, 'admin', '1234', 'admin@example.com', 'แอดมิน', '', '', '2019-05-04 07:41:34'),
(2, 'nan', '1234', 'nan@gmail.com', 'ณัฐมาศ', 'สันสาคร', '406/1 ต.ปากพะยูน อ.ปากพะยูน จ.พัทลุง 93120', '2019-05-12 06:22:38'),
(3, 'fundy', '123456', 'fiwdunfundy@gmail.com', 'panut', 'sansakorn', 'aa', '2019-05-15 17:02:54'),
(4, 'kittitas', '1234', 'dream@gmail.com', 'kittitas', 'chaisawed', '123', '2019-05-16 02:32:24'),
(5, 'areerat', '1234', 'areerat@gmail.com', 'areerat', 'dokmai', '456', '2019-05-16 02:33:58'),
(6, 'nithi', '1234', 'nithi@gmail.com', 'nithi', 'mangmee', '789', '2019-05-16 02:35:51'),
(7, 'pruksa', '1234', 'pruksa@gmail.com', 'pruksa', 'srikongton', '999', '2019-05-16 02:38:35'),
(8, 'bank', 'banks', 'banks@gmail.com', 'adisak', 'kongsang', '20/5  ถนนกาญจนาภิเษก แขวง ทวีวัฒนา เขต ทวีวัฒนา จังหวัดกรุงเทพมหานคร', '2019-05-16 04:00:05'),
(9, 'cameherez', 'vongolaza', 'cameherez@gmail.com', 'bertong', 'kamnong', '444', '2019-05-16 04:57:23'),
(10, 'new', '1234', 'new@gmail.com', 'suwimon', 'poranok', '77', '2019-05-17 04:03:09');

-- --------------------------------------------------------

--
-- Table structure for table `water_quality`
--

CREATE TABLE `water_quality` (
  `id` int(11) NOT NULL,
  `pond_id` int(11) NOT NULL,
  `test_date` date NOT NULL,
  `ph_morning` float DEFAULT NULL,
  `ph_evening` float DEFAULT NULL,
  `salty` float DEFAULT NULL,
  `ammonia` float DEFAULT NULL,
  `nitrite` float DEFAULT NULL,
  `alkaline` float DEFAULT NULL,
  `calcium` float DEFAULT NULL,
  `magnesium` float DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

--
-- Dumping data for table `water_quality`
--

INSERT INTO `water_quality` (`id`, `pond_id`, `test_date`, `ph_morning`, `ph_evening`, `salty`, `ammonia`, `nitrite`, `alkaline`, `calcium`, `magnesium`, `created_at`) VALUES
(1, 9, '2019-05-20', 1, 5, 3, 4, 5, 6, 7, 8, '2019-05-20 15:59:52'),
(2, 9, '2019-05-21', 7.5, 8, 10, 200, 500, 210, 1200, 500, '2019-05-21 01:13:07');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cycle`
--
ALTER TABLE `cycle`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `farm`
--
ALTER TABLE `farm`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `feeding`
--
ALTER TABLE `feeding`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `hatchery`
--
ALTER TABLE `hatchery`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pond`
--
ALTER TABLE `pond`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `water_quality`
--
ALTER TABLE `water_quality`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cycle`
--
ALTER TABLE `cycle`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `farm`
--
ALTER TABLE `farm`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `feeding`
--
ALTER TABLE `feeding`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `hatchery`
--
ALTER TABLE `hatchery`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pond`
--
ALTER TABLE `pond`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `water_quality`
--
ALTER TABLE `water_quality`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
