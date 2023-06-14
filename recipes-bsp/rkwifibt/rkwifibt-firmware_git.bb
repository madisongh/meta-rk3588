DESCRIPTION = "Rockchip Wi-fi/Bluetooth firmware"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=919c961282a1817c7f9a6bf495fa7b2e \
                    file://debian/copyright;md5=430aaa3a286d37e6fb223b9cf2582553"

require rkwifibt.inc

PV = "1.0+git${SRCPV}"

do_compile() {
    :
}
do_install() {
    install -D -m 0644 -t ${D}${nonarch_base_libdir}/firmware/ ${S}/firmware/realtek/RTL8852BS/*
}

FILES:${PN} = "${nonarch_base_libdir}/firmware"
