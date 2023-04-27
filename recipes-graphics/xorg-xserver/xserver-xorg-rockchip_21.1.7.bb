require recipes-graphics/xorg-xserver/xserver-xorg.inc
SRC_REPO = "github.com/JeffyCN/xorg-xserver;protocol=https"
SRC_URI = "git://${SRC_REPO};nobranch=1"
# 21.1.7_2022_11_03 tag
SRCREV = "61e9e0d201be69694cea95f4e0d42c6d97236333"
PV .= "+git${SRCPV}"

SRC_URI += "file://0001-Fix-unresolved-references-to-glamor-symbols.patch"
SRC_URI += "file://20-modesetting.conf"

DEPENDS += "rockchip-librga"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

PROVIDES += "xserver-xorg"

inherit rockchip_uapi

S = "${WORKDIR}/git"

do_install:append() {
    install -d ${D}${datadir}/X11/xorg.conf.d
    install -m 0755 ${WORKDIR}/20-modesetting.conf \
        ${D}${datadir}/X11/xorg.conf.d/20-modesetting.conf
}

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
