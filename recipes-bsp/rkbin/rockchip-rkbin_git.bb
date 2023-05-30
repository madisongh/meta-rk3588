DESCRIPTION = "Rokchip boot firmware binaries, configs, tools"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

SRC_REPO = "gitlab.com/firefly-linux/rkbin.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "43c2ce3ce3abe1163cca58c204f9ead6799634b4"

PV = "1.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"
COMPATIBLE_MACHINE:class-native = ""

PROVIDES:class-native = "rockchip-rkbin-tools-native"

inherit nopackages

S = "${WORKDIR}/git"

RKBIN_SUBDIR = "${@d.getVar('SOC_FAMILY')[0:4]}"
RKBIN_PREFIX = "${SOC_FAMILY}"
# Need binaries prefixed with rk3566, rk3568, and rk356x
RKBIN_PREFIX:rk3568 = "rk356"
RKBIN_INI_PREFIX = "${@d.getVar('RKBIN_PREFIX').upper()}"

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
    install -d ${D}${bindir}/rkbin-tools
    for f in ${S}/tools/*; do
        [ -f "$f" ] || continue
        if [ "${f%%.ini}" != "$f" -o "${f%%.txt}" != "$f" ]; then
            install -m 0644 "$f" ${D}${bindir}/rkbin-tools/
        else
            install -m 0755 "$f" ${D}${bindir}/rkbin-tools/
        fi
    done
}

BBCLASSEXTEND = "native"
PACKAGE_ARCH:class-target = "${SOC_FAMILY_PKGARCH}"
