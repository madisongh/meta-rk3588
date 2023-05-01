# Copyright (C) 2022, Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

SRC_REPO = "github.com/madisongh/camera-engine-rkaiq.git;protocol=https"
SRCBRANCH = "patches-v3.0x9.1"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://rkaiq_3A.init \
           file://rkaiq_3A.service \
"
SRCREV = "2e7b86441e25770b696ab6ca5a0d9298ea53333a"
PV = "3.0-9.1+git${SRCPV}"

DEPENDS = "coreutils-native xxd-native rockchip-librga libdrm v4l-utils"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit pkgconfig cmake rockchip_uapi

RK_ISP_VERSION = ""
RK_ISP_VERSION:rk3588 = "ISP_HW_V30"

EXTRA_OECMAKE = "     \
    -DARCH=${@bb.utils.contains('TUNE_FEATURES', 'aarch64', 'aarch64', 'arm', d)} \
    -DISP_HW_VERSION=-D${RK_ISP_VERSION} \
    -DIQ_PARSER_V2_EXTRA_CFLAGS='-I${STAGING_INCDIR}/rockchip-uapi;-I${STAGING_INCDIR}' \
    -DRKAIQ_TARGET_SOC=${SOC_FAMILY} \
"

do_install:append () {
	install -d ${D}${sysconfdir}/iqfiles
	install -m 0644 ${S}/iqfiles/*/*.json ${D}${sysconfdir}/iqfiles/

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/rkaiq_3A.init ${D}${sysconfdir}/init.d/rkaiq_3A
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/rkaiq_3A.service ${D}${systemd_system_unitdir}/
}

inherit update-rc.d systemd

INITSCRIPT_PACKAGES = "${PN}-server"
INITSCRIPT_NAME:${PN}-server = "rkaiq_daemons.sh"
INITSCRIPT_PARAMS:${PN}-server = "start 70 5 4 3 2 . stop 30 0 1 6 ."

PACKAGES =+ "${PN}-server ${PN}-demo ${PN}-iqfiles"
SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE:${PN}-server = "rkaiq_3A.service"
FILES:${PN}-dev = "${includedir}"
FILES:${PN}-demo = "${bindir}/rkisp_demo"
FILES:${PN}-server = " \
	${bindir}/rkaiq_3A_server \
	${sysconfdir}/init.d/ \
"
FILES:${PN}-iqfiles = "${sysconfdir}/iqfiles/"
FILES:${PN} = "${libdir}"
RDEPENDS:${PN}-server = "${PN} ${PN}-iqfiles"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
