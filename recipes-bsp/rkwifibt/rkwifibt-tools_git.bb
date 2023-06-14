DESCRIPTION = "Rockchip Wi-fi/Bluetooth userland tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://tools/rtk_hciattach/hciattach_rtk.c;endline=13;md5=3d8c68f56258fa43ee25834e0ae01574"

require rkwifibt.inc

SRC_URI += "\
    file://rk-bluetooth-init.sh.in \
    file://rk-bluetooth-init.service.in \
"

PV = "3.1.390bad8.20220519-142434"

inherit systemd

EXTRA_OEMAKE = '-C tools/rtk_hciattach CC="${CC} ${LDFLAGS}" CFLAGS="${CFLAGS}"'

patch_firmware_path() {
    sed -i -e's,/lib/firmware/rtlbt/,${nonarch_base_libdir}/firmware/,g' ${S}/tools/rtk_hciattach/rtb_fwc.c
}
do_patch[postfuncs] += "patch_firmware_path"

do_compile() {
    oe_runmake
    sed -e 's,@BT_TTY_DEV@,${RK_BT_TTY_DEV},' ${WORKDIR}/rk-bluetooth-init.sh.in > ${B}/rk-bluetooth-init.sh
    sed -e 's,@SBINDIR@,${sbindir},' -e 's,@BASEBINDIR@,${base_bindir},' \
        ${WORKDIR}/rk-bluetooth-init.service.in > ${B}/rk-bluetooth-init.service
}

do_install() {
    install -D -m 0755 tools/rtk_hciattach/rtk_hciattach ${D}${bindir}/rtk_hciattach
    install -D -m 0755 bin/arm64/rtwpriv ${D}${bindir}/rtwpriv
    install -D -m 0755 rk-bluetooth-init.sh ${D}${sbindir}/rk-bluetooth-init
    install -D -m 0644 rk-bluetooth-init.service ${D}${systemd_system_unitdir}/rk-bluetooth-init.service
}

PACKAGES =+ "${PN}-bluetooth ${PN}-wifi"
SYSTEMD_PACKAGES = "${PN}-bluetooth"
SYSTEMD_SERVICE:${PN}-bluetooth = "rk-bluetooth-init.service"
FILES:${PN}-bluetooth = "${bindir}/rtk_hciattach ${sbindir}/rk-bluetooth-init"
FILES:${PN}-wifi = "${bindir}/rtwpriv"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN}-bluetooth ${PN}-wifi"
RDEPENDS:${PN}-bluetooth = "rkwifibt-bt-driver rkwifibt-firmware"
RDEPENDS:${PN}-wifi = "rkwifibt-wifi-driver rkwifibt-firmware"
