DESCRIPTION = "Rockchip/Realtek out-of-tree driver for rtl8723ds"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://os_dep/linux/os_intfs.c;endline=14;md5=8092f02f993180bbafbb00f98844d66c"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/rtl8723ds"

inherit module

MAKE_TARGETS = "-C ${STAGING_KERNEL_DIR} M=${S} modules CONFIG_RTW_DEBUG=n CONFIG_PROC_DEBUG=n CONFIG_RTW_LOG_LEVEL=0"
MAKE_TARGETS += 'USER_EXTRA_CFLAGS="-Wno-misleading-indentation"'
MODULES_INSTALL_TARGET = "-C ${STAGING_KERNEL_DIR} M=${S} modules_install"
