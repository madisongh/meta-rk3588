require recipes-graphics/xorg-xserver/xserver-xorg.inc
S = "${UNPACKDIR}/${BP}"
SRC_REPO = "github.com/JeffyCN/xorg-xserver;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=21.1.23"
# untagged
SRCREV = "f24d2746a698b523b034200992df054423ce4e7d"
PV .= "+git"

SRC_URI += "file://20-modesetting.conf"

DEPENDS += "rockchip-librga"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PROVIDES += "xserver-xorg"

inherit rockchip_uapi


do_install:append() {
    install -d ${D}${datadir}/X11/xorg.conf.d
    install -m 0755 ${UNPACKDIR}/20-modesetting.conf \
        ${D}${datadir}/X11/xorg.conf.d/20-modesetting.conf
}

RPROVIDES:${PN}-module-exa += "xserver-xorg-module-exa"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
