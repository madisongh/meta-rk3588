# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require u-boot-rockchip-downstream.inc

PROVIDES += "u-boot"

addtask uboot_assemble_fitimage before do_deploy after do_compile do_populate_sysroot do_setup_rkbin
addtask rk_sign_bootloader after do_uboot_assemble_fitimage before do_deploy
