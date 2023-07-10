DESCRIPTION = "Tools and scripts for Rockchip WiFi/BT drivers"
LICENSE = "BSD-3-Clause & Apache-2.0 & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=919c961282a1817c7f9a6bf495fa7b2e \
                    file://tools/brcm_tools/brcm_patchram_plus1.c;endline=17;md5=691691b063f1b4034300dc452e36b68d \
                    file://tools/rtk_hciattach/hciattach_rtk.c;endline=13;md5=3d8c68f56258fa43ee25834e0ae01574"


require rkwifibt.inc

PV = "1.0+git${SRCPV}"

inherit meson

do_compile() {
    meson_do_compile
    oe_runmake -C ${S}/tools/rtk_hciattach rtk_hciattach CFLAGS="${CFLAGS}" CC="${CC} ${LDFLAGS}"
}

do_clean() {
    meson_do_clean
    oe_runmake -C ${S}/tools/rtk_hciattach clean
}

do_install() {
    meson_do_install
    install -D -m 0755 -t ${D}${bindir} ${S}/tools/rtk_hciattach/rtk_hciattach
}

PACKAGES =+ "${PN}-brcm ${PN}-rtk ${PN}-rockchip-wifibt-init"
FILES:${PN}-brcm = "${bindir}/brcm_patchram_plus1 ${bindir}/dhd_priv"
FILES:${PN}-rtk = "${bindir}/rtk_hciattach"
FILES:${PN}-rockchip-wifibt-init = "${bindir}/rk_wifibt_init"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN}-brcm ${PN}-rtk"
