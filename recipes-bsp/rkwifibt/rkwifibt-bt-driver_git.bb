DESCRIPTION = "Rockchip Wi-Fi driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://hci_ldisc.c;endline=24;md5=2afc1043c3931e9167681f36ec4c73e6"

PV = "2.2.3634cd9.20220519-142433"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/bluetooth_uart_driver"

EXTRA_OEMAKE += "-C ${STAGING_KERNEL_DIR} M=${S} ARCH=${ARCH} CROSS_COMPILE=${TARGET_PREFIX}"

inherit module
