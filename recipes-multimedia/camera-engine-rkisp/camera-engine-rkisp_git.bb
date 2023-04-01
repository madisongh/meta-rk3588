# Copyright (C) 2019, Fuzhou Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DEPENDS = "coreutils-native libdrm gstreamer1.0 gstreamer1.0-plugins-base glib-2.0"

SRC_REPO = "github.com/madisongh/camera_engine_rkisp.git;protocol=https"
SRCBRANCH = "patches-1.0"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
	   file://rkisp_daemons.sh \
"
SRCREV = "7321809f977ad269dda82e03778cb6ba8b72e03f"
PV = "1.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

MAKE_TARGET_ARCH = "UNKNOWN"
PREBUILTLIBDIR = "UNKNOWN"
MAKE_TARGET_ARCH:arm = "arm"
PREBUILTLIBDIR:arm = "lib32"
MAKE_TARGET_ARCH:aarch64 = "aarch64"
PREBUILTLIBDIR:aarch64 = "lib64"

EXTRA_OEMAKE = 'V=1 OUTDIR=${B} ARCH="${MAKE_TARGET_ARCH}" TARGET_GCC="${CC} ${CFLAGS} ${LDFLAGS}" TARGET_GPP="${CXX} ${CPPFLAGS} ${LDFLAGS} -Wno-error=cpp" TARGET_LD="${LD} ${LDFLAGS}" TARGET_AR="${AR}"'


inherit update-rc.d pkgconfig

do_configure() {
	:
}

do_compile[dirs] = "${S}"
do_compile[cleandirs] = "${B}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/bin/rkisp_demo ${D}${bindir}
	install -m 0755 ${B}/bin/rkisp_3A_server ${D}${bindir}

	install -d ${D}${sysconfdir}/iqfiles
	install -m 0644 ${S}/iqfiles/*.xml ${D}${sysconfdir}/iqfiles/

	install -d ${D}${libdir}
	install -m 0644 ${B}/lib/librkisp.so ${D}${libdir}
	install -m 0644 ${B}/lib/librkisp_api.so ${D}${libdir}

	install -d ${D}${libdir}/rkisp/ae
	install -m 0644 ${S}/plugins/3a/rkiq/aec/${PREBUILTLIBDIR}/librkisp_aec.so \
		${D}${libdir}/rkisp/ae/

	install -d ${D}${libdir}/rkisp/af
	install -m 0644 ${S}/plugins/3a/rkiq/af/${PREBUILTLIBDIR}/librkisp_af.so \
		${D}${libdir}/rkisp/af/

	install -d ${D}${libdir}/rkisp/awb
	install -m 0644 ${S}/plugins/3a/rkiq/awb/${PREBUILTLIBDIR}/librkisp_awb.so \
		${D}${libdir}/rkisp/awb/

	install -d ${D}${includedir}/camera_engine_rkisp/interface
	install -m 0644 ${S}/interface/*.h \
		${D}${includedir}/camera_engine_rkisp/interface/

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/rkisp_daemons.sh ${D}${sysconfdir}/init.d/

	install -d ${D}${libdir}/gstreamer-1.0
	install -m 0644 ${B}/lib/libgstrkisp.so ${D}${libdir}/gstreamer-1.0/
}


INITSCRIPT_PACKAGES = "${PN}-server"
INITSCRIPT_NAME:${PN}-server = "rkisp_daemons.sh"
INITSCRIPT_PARAMS:${PN}-server = "start 70 5 4 3 2 . stop 30 0 1 6 ."

INSANE_SKIP:${PN} = "already-stripped ldflags"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
PACKAGES =+ "${PN}-tests ${PN}-server ${PN}-iqfiles gstreamer1.0-plugins-rkisp"
FILES:gstreamer1.0-plugins-rkisp = "${libdir}/gstreamer-1.0"
FILES:${PN} += "${libdir}/rkisp"
FILES:${PN}-tests = "${bindir}/rkisp_demo"
FILES:${PN}-server = " \
	${bindir}/rkisp_3A_server \
	${sysconfdir}/init.d/ \
"
FILES:${PN}-iqfiles = "${sysconfdir}/iqfiles/"
RDEPENDS:${PN}-tests = "${PN}"
RDEPENDS:${PN}-server = "${PN}"
RDEPENDS:gstreamer1.0-plugins-rkisp = "${PN}"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
