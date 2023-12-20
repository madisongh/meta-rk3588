DESCRIPTION = "Rokchip boot firmware binaries, configs, tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

SRC_REPO = "gitlab.com/firefly-linux/rkbin.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "aba33eb6f88991d2603d91e1afbe28165cf6e8ba"

PV = "1.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"
COMPATIBLE_MACHINE:class-native = ""
COMPATIBLE_MACHINE:class-nativesdk = ""

PROVIDES:class-native = "rockchip-rkbin-tools-native"
PROVIDES:class-nativesdk = "nativesdk-rockchip-rkbin-tools"

S = "${WORKDIR}/git"

RKBIN_SUBDIR = "${@d.getVar('SOC_FAMILY')[0:4]}"
RKBIN_PREFIX = "${SOC_FAMILY}"
# Need binaries prefixed with rk3566, rk3568, and rk356x
RKBIN_PREFIX:rk3568 = "rk356"
RKBIN_INI_PREFIX = "${@d.getVar('RKBIN_PREFIX').upper()}"

TOOLS_BINARIES = "boot_merger trust_merger loaderimage rk_sign_tool"
TOOLS_FILES = "setting.ini"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR} ${D}${datadir}/rkbin/RKBOOT ${D}${datadir}/rkbin/RKTRUST
    install -m 0644 ${S}/bin/${RKBIN_SUBDIR}/${RKBIN_PREFIX}* ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR}/
    install -m 0644 ${S}/RKBOOT/${RKBIN_INI_PREFIX}*.ini ${D}${datadir}/rkbin/RKBOOT/
    install -m 0644 ${S}/RKTRUST/${RKBIN_INI_PREFIX}*.ini ${D}${datadir}/rkbin/RKTRUST/
}

do_install:class-native() {
    install_tools
}

do_install:class-nativesdk() {
    install_tools
}

install_tools() {
    install -d ${D}${bindir}/rkbin-tools
    for f in ${TOOLS_BINARIES}; do
	install -m 0755 ${S}/tools/$f ${D}${bindir}/rkbin-tools/
    done
    for f in ${TOOLS_FILES}; do
	install -m 0644 ${S}/tools/$f ${D}${bindir}/rkbin-tools/
    done
}

PACKAGES =+ "${PN}-tools"
ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-tools = "1"
FILES:${PN} = "${datadir}/rkbin"
FILES:${PN}-tools = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
PACKAGE_ARCH:class-target = "${SOC_FAMILY_PKGARCH}"
