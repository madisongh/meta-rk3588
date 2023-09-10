# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

UBOOT_MACHINE = "UNKNOWN"
UBOOT_MACHINE:rk3588 = "rk3588_defconfig"
UBOOT_MACHINE:rk3568 = "rockchip-usbplug_defconfig"

require u-boot-rockchip-downstream.inc

PROVIDES = "rkusbloader"

SRC_URI:append:rk3588 = " file://rk3588-ipc.cfg"
SRC_URI:append:rk3568 = " file://rk3568-usbplug.cfg"

do_install() {
    :
}
do_install[noexec] = "1"

addtask assemble_rkusbloader after do_compile before do_deploy
addtask rk_sign_usbloader after do_assemble_rkusbloader before do_deploy

do_deploy() {
    deploy_rockchip_loader
}
