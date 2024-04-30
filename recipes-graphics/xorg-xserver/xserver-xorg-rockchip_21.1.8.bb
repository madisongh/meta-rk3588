require recipes-graphics/xorg-xserver/xserver-xorg.inc
SRC_REPO = "github.com/JeffyCN/xorg-xserver;protocol=https"
SRC_URI = "git://${SRC_REPO};nobranch=1"
# 21.1.8_2024_01_31 tag
SRCREV = "19ac81ee935825dff2a16b680bbc66561e70eb7c"
PV .= "+git${SRCPV}"

SRC_URI += "file://0001-Fix-unresolved-references-to-glamor-symbols.patch"
SRC_URI += "file://20-modesetting.conf"

DEPENDS += "rockchip-librga"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PROVIDES += "xserver-xorg"

inherit rockchip_uapi

S = "${WORKDIR}/git"

do_install:append() {
    install -d ${D}${datadir}/X11/xorg.conf.d
    install -m 0755 ${WORKDIR}/20-modesetting.conf \
        ${D}${datadir}/X11/xorg.conf.d/20-modesetting.conf
}

RPROVIDES:${PN}-module-exa += "xserver-xorg-module-exa"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
