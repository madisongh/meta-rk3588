DESCRIPTION = "Rockchip Bluetooth driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://os_dep/linux/os_intfs.c;endline=19;md5=adfd12b22ba01710d122215dd09bb91f"

PV = "1.15.9.2-11-gedded47.20220606"

require rkwifibt.inc

S = "${WORKDIR}/git/drivers/rtl8852bs"

EXTRA_OEMAKE += "KSRC=${STAGING_KERNEL_DIR} ARCH=${ARCH} CROSS_COMPILE=${TARGET_PREFIX}"

inherit module

do_install() {
    install -D -m 0644 8852bs.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/8852bs.ko
    install -D -m 0644 Module.symvers ${D}${includedir}/${BPN}/Module.symvers
    sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
}
