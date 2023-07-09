DESCRIPTION = "Rockchip out-of-tree drivers for supported WiFi/BT devices"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://os_dep/linux/os_intfs.c;endline=14;md5=65d26201a0fa31fa29d00e3b083e5c28"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/rtl8852bs"

inherit module

MAKE_TARGETS = "-C ${STAGING_KERNEL_DIR} M=${S} modules DRV_PATH=${S}"
MODULES_INSTALL_TARGET = "-C ${STAGING_KERNEL_DIR} M=${S} modules_install"
