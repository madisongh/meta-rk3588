# Copyright (c) 2023, M. Madison
# SPDX-License-Identifier: MIT
SOC_FAMILY = "rk3568"
require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc
require conf/machine/include/rockchip-common.inc

UBOOT_MACHINE ?= "rk3566_defconfig"
UBOOT_ENTRYPOINT = "0x00280000"
UBOOT_RD_LOADADDRESS = "0x0a200000"
UBOOT_DTB_LOADADDRESS = "0xa100000"
SERIAL_CONSOLES ?= "1500000;ttyFIQ0"

RK_TARGET_SOC = "rk356x"

MALI_GPU = "bifrost-g52"
MALI_VERSION = "g2p0"
