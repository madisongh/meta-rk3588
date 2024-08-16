DESCRIPTION = "Rockchip/Realtek out-of-tree driver for rtl8822cs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://os_dep/linux/os_intfs.c;endline=14;md5=8092f02f993180bbafbb00f98844d66c"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/rtl8822cs"

inherit module

MAKE_TARGETS = "V=1 -C ${STAGING_KERNEL_DIR} M=${S} modules DRV_PATH=${S} CONFIG_RTW_DEBUG=n CONFIG_PROC_DEBUG=n CONFIG_RTW_LOG_LEVEL=0"
MAKE_TARGETS += 'USER_EXTRA_CFLAGS="-Wno-error=misleading-indentation -Wno-error=unused-function"'
MODULES_INSTALL_TARGET = "-C ${STAGING_KERNEL_DIR} M=${S} modules_install"
