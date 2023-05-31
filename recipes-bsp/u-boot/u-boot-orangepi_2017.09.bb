# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require u-boot-rockchip-downstream.inc
require u-boot-orangepi-source.inc

COMPATIBLE_MACHINE:rockchip = "(-)"
COMPATIBLE_MACHINE:orangepi5 = "(orangepi5)"

PROVIDES += "u-boot"

do_compile:append() {
    make_rockchip_binaries
}

do_deploy:append() {
    deploy_rockchip_binaries
}
