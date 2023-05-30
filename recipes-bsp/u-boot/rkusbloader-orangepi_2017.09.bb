# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

UBOOT_MACHINE = "rk3588_defconfig"

require u-boot-rockchip-downstream.inc
require u-boot-orangepi-source.inc

PROVIDES = "rksubloader"

SRC_URI += "file://rk3588-ipc.cfg"

do_compile:append() {
    make_rockchip_loader
}

do_deploy:append() {
    deploy_rockchip_loader
}
