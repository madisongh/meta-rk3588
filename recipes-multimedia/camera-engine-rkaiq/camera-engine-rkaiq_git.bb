# Copyright (C) 2022, Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRC_REPO = "gitlab.com/firefly-linux/external/camera_engine_rkaiq.git;protocol=https"
SRCBRANCH = "rk356x/firefly-5.10"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-iq_parser_v2-add-variable-for-extra-compiler-flags.patch \
           file://0002-Fix-install-paths.patch \
           file://rkaiq_3A.init \
           file://rkaiq_3A.service \
"
SRCREV = "711f03c4be098b2083dbdf8b7d8dbbba76bb012c"
PV = "5.0-1.3+git${SRCPV}"

DEPENDS = "coreutils-native xxd-native rockchip-librga libdrm v4l-utils"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit pkgconfig cmake rockchip_uapi

RK_ISP_VERSION = ""
RK_ISP_VERSION:rk3588 = "ISP_HW_V30"
IQFILES_SUBDIR = ""
IQFILES_SUBDIR:rk3588 = "isp3x"
RK_ISP_VERSION:rk3568 = "ISP_HW_V21"
IQFILES_SUBDIR:rk3568 = "isp21"

EXTRA_OECMAKE = "     \
    -DARCH=${@bb.utils.contains('TUNE_FEATURES', 'aarch64', 'aarch64', 'arm', d)} \
    -DISP_HW_VERSION=-D${RK_ISP_VERSION} \
    -DIQ_PARSER_V2_EXTRA_CFLAGS='-I${STAGING_INCDIR}/rockchip-uapi;-I${STAGING_INCDIR}' \
    -DRKAIQ_TARGET_SOC=${RK_TARGET_SOC} \
"

do_install:append () {
	install -d ${D}${sysconfdir}/iqfiles
	install -m 0644 ${S}/rkaiq/iqfiles/${IQFILES_SUBDIR}/*.json ${D}${sysconfdir}/iqfiles/

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
