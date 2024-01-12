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

RK_OPTEE_SIGNING_PUBKEY ??= ""
RK_OPTEE_SIGNING_CLASS ??= ""
RK_OPTEE_TOOLSDEP = "${@'rk-optee-signing-tools-native' if d.getVar('RK_OPTEE_SIGNING_PUBKEY') else ''}"
RK_OPTEE_TOOLSDEP:class-native = ""
RK_OPTEE_TOOLSDEP:class-nativesdk = ""
DEPENDS = "${RK_OPTEE_TOOLSDEP}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

RKBIN_SUBDIR = "${@d.getVar('SOC_FAMILY')[0:4] if d.getVar('SOC_FAMILY') else ''}"
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
    cp ${S}/bin/${RKBIN_SUBDIR}/${RKBIN_PREFIX}*bl32*.bin ${B}/
}

do_sign_bl32() {
    if [ -n "${RK_OPTEE_SIGNING_PUBKEY}" ]; then
        for f in ${RKBIN_PREFIX}*bl32*bin; do
            change_puk --teebin $f --key "${RK_OPTEE_SIGNING_PUBKEY}"
        done
    fi
}
do_sign_bl32[dirs] = "${B}"

# Placed here to allow a user-provided signing class to override the
# do_sign_bl32 function more easily
inherit ${RK_OPTEE_SIGNING_CLASS}

do_sign_bl32:class-native() {
    :
}

do_sign_bl32:class-nativesdk() {
    :
}

addtask sign_bl32 before do_install after do_compile

do_install() {
    install -d ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR} ${D}${datadir}/rkbin/RKBOOT ${D}${datadir}/rkbin/RKTRUST
    install -m 0644 ${S}/bin/${RKBIN_SUBDIR}/${RKBIN_PREFIX}* ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR}/
    for inf in ${B}/${RKBIN_PREFIX}*bl32*.bin; do
        outf=$(basename $inf)
        rm -f ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR}/$outf
	install -m 0644 $inf ${D}${datadir}/rkbin/bin/${RKBIN_SUBDIR}/$outf
    done
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
PACKAGE_ARCH:class-target = "${MACHINE_ARCH}"
