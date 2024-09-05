DESCRIPTION = "Rockchip tools needed for creating a flashable update package"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

SRC_REPO = "gitlab.com/firefly-linux/tools.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "9c1b0a4ce7f9f38dac3ae3389934f0a340740e11"

PV = "1.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:class-native = ""
COMPATIBLE_MACHINE:class-nativesdk = ""

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -D -m 0755 -t ${D}${bindir} \
	    ${S}/linux/Linux_Pack_Firmware/rockdev/afptool \
	    ${S}/linux/Linux_Pack_Firmware/rockdev/rkImageMaker
}

BBCLASSEXTEND = "native nativesdk"

