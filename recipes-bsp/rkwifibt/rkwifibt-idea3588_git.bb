DESCRIPTION = "Rockchip Wi-fi/Bluetooth support for the Boardcon Idea3588 development board"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=919c961282a1817c7f9a6bf495fa7b2e \
                    file://debian/copyright;md5=430aaa3a286d37e6fb223b9cf2582553"

SRC_REPO = "github.com/madisongh/rkwifibt-idea3588.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "7e3da7a5a5ff96697f7bd07504c7fe2b24403dcc"

SRC_URI += "\
    file://rk-bluetooth-init.sh.in \
    file://rk-bluetooth-init.service.in \
"

PV = "1.0+git${SRCPV}"

PROVIDES = "rkwifibt"
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:idea3588 = "(idea3588)"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit meson systemd

do_compile() {
    meson_do_compile
    sed -e 's,@BTFIRMWARE_PATH@,${nonarch_base_libdir}/firmware/BCM4362A2.hcd,' -e 's,@BT_TTY_DEV@,${RK_BT_TTY_DEV},' \
        ${WORKDIR}/rk-bluetooth-init.sh.in > ${B}/rk-bluetooth-init.sh
    sed -e 's,@SBINDIR@,${sbindir},' -e 's,@BASEBINDIR@,${base_bindir},' \
        ${WORKDIR}/rk-bluetooth-init.service.in > ${B}/rk-bluetooth-init.service
}

do_install() {
    meson_do_install
    rm -rf ${D}/system
    rm -f ${D}${bindir}/rk_wifi_init
    install -D -t ${D}${nonarch_base_libdir}/firmware -m 0644 ${S}/firmware/broadcom/AP6398SV/bt/*
    install -D -t ${D}${nonarch_base_libdir}/firmware -m 0644 ${S}/firmware/broadcom/AP6398SV/wifi/*
    install -D -t ${D}${systemd_system_unitdir} -m 0644 ${B}/rk-bluetooth-init.service
    install -D --m 0755 ${B}/rk-bluetooth-init.sh ${D}${sbindir}/rk-bluetooth-init
}

PACKAGES =+ "${PN}-bluetooth ${PN}-wifi"
SYSTEMD_PACKAGES = "${PN}-bluetooth"
SYSTEMD_SERVICE:${PN}-bluetooth = "rk-bluetooth-init.service"
FILES:${PN}-bluetooth = "${sbindir}/rk-bluetooth-init ${bindir}/brcm_patchram_plus1 ${nonarch_base_libdir}/firmware/*.hcd"
FILES:${PN}-wifi = "${bindir}/dhd_priv ${nonarch_base_libdir}/firmware"
RRECOMMENDS:${PN}-wifi = "kernel-module-bcmdhd"
ALLOW_EMPTY:${PN} = "1"
RPROVIDES:${PN} = "rkwifibt"
RPROVIDES:${PN}-bluetooth = "rkwifibt-bluetooth"
RPROVIDES:${PN}-wifi = "rkwifibt-wifi"

PACKAGE_ARCH = "${MACHINE_ARCH}"
