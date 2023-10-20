DESCRIPTION = "Rockchip/Broadcom out-of-tree bcmdhd driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://Makefile;endline=1;md5=daad6f7f7a0a286391cd7773ccf79340"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/bcmdhd"

inherit module

MAKE_TARGETS = "-C ${STAGING_KERNEL_DIR} M=${S} modules CONFIG_BCMDHD=m CONFIG_BCMDHD_SDIO=y CONFIG_BCMDHD_PCIE= CONFIG_BCMDHD_REQUEST_FW=y"
MODULES_INSTALL_TARGET = "-C ${STAGING_KERNEL_DIR} M=${S} modules_install"
