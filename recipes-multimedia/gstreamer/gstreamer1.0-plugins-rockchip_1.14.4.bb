DESCRIPTION = "GStreamer 1.0 plugins for Rockchip SoCs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS += "gstreamer1.0-plugins-base"

SRC_REPO = "github.com/madisongh/gstreamer-rockchip.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "2ed1e68b0aa77728b1d493344d8e62a04b1b64e0"
PV .= "+git${SRCPV}"

PACKAGECONFIG ??= "mpp ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} rga"
PACKAGECONFIG[mpp] = "-Drockchipmpp=enabled,-Drockchipmpp=disabled,rockchip-mpp"
PACKAGECONFIG[x11] = "-Drkximage=enabled,-Drkximage=disabled,libx11 libdrm"
PACKAGECONFIG[rga] = "-Drga=enabled,-Drga=disabled,rockchip-librga"

S = "${WORKDIR}/git"

inherit meson pkgconfig

require recipes-multimedia/gstreamer/gstreamer1.0-plugins-packaging.inc
