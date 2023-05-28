# Copyright (c) 2023, M. Madison
# SPDX-License-Identifier: MIT
SOC_FAMILY = "rk3588"
require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/armv8-2a/tune-cortexa76-cortexa55.inc
require conf/machine/include/rockchip-common.inc

UBOOT_MACHINE ?= "rk3588_defconfig"
SERIAL_CONSOLES ?= "1500000;ttyFIQ0"

RK_TARGET_SOC = "rk3588"

MALI_GPU = "valhall-g610"
MALI_VERSION = "g6p0"
