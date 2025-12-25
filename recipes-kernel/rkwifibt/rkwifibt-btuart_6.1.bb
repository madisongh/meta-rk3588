DESCRIPTION = "Rockchip/Realtek out-of-tree hci_uart driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://hci_uart.h;endline=24;md5=2afc1043c3931e9167681f36ec4c73e6"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/bluetooth_uart_driver"

inherit module

MAKE_TARGETS = "-C ${STAGING_KERNEL_DIR} M=${S} modules"
MODULES_INSTALL_TARGET = "-C ${STAGING_KERNEL_DIR} M=${S} modules_install"
