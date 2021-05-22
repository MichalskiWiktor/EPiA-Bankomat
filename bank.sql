-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 22, 2021 at 07:09 PM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 8.0.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bank`
--

-- --------------------------------------------------------

--
-- Table structure for table `administrators`
--

CREATE TABLE `administrators` (
  `id` int(11) NOT NULL,
  `card_number` int(9) DEFAULT NULL,
  `pin` int(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;

--
-- Dumping data for table `administrators`
--

INSERT INTO `administrators` (`id`, `card_number`, `pin`) VALUES
(1, 111111111, 1111);

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `card_number` int(9) DEFAULT NULL,
  `pin` int(4) DEFAULT NULL,
  `ammount` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `card_number`, `pin`, `ammount`) VALUES
(1, 123456789, 1111, 4901),
(2, 987654321, 1234, 1100),
(3, 123498765, 4321, 100011);

-- --------------------------------------------------------

--
-- Table structure for table `operation_history`
--

CREATE TABLE `operation_history` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_polish_ci DEFAULT NULL,
  `ammount` int(11) DEFAULT NULL,
  `type` varchar(22) COLLATE utf8mb4_polish_ci DEFAULT NULL,
  `date` date DEFAULT NULL,
  `id_customer` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci;

--
-- Dumping data for table `operation_history`
--

INSERT INTO `operation_history` (`id`, `name`, `ammount`, `type`, `date`, `id_customer`) VALUES
(1, 'brak', 5000, 'wyplata', '2021-05-12', 3),
(2, 'brak', 5000, 'wyplata', '2021-05-12', 1),
(3, 'brak', 100, 'wyplata', '2021-05-15', 2),
(4, 'brak', 999, 'wplata', '2021-05-20', 1),
(5, 'brak', 111, 'wplata', '2021-05-20', 1),
(6, 'brak', 1111, 'wyplata', '2021-05-20', 1),
(13, 'przelew za zamówienie nr.4', 555, 'przelew wychodzący', '2021-05-20', 1),
(14, 'przelew za zamówienie nr.4', 555, 'przelew przychodzący', '2021-05-20', 2),
(16, 'brak', 11, 'wplata', '2021-05-21', 3),
(17, 'brak', 99, 'wyplata', '2021-05-21', 1),
(18, 'brak', 100, 'wplata', '2021-05-22', 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `administrators`
--
ALTER TABLE `administrators`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `operation_history`
--
ALTER TABLE `operation_history`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `administrators`
--
ALTER TABLE `administrators`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `operation_history`
--
ALTER TABLE `operation_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
