DESCRIPTION = "GStreamer 1.0 plugins for Rockchip SoCs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS += "gstreamer1.0-plugins-base"

SRC_REPO = "github.com/madisongh/gstreamer-rockchip.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "0fc75d3710fede7ca2dfb4687362fb6979100d9e"
PV .= "+git${SRCPV}"

PACKAGECONFIG ??= "mpp ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} rga"
PACKAGECONFIG[mpp] = "-Drockchipmpp=enabled,-Drockchipmpp=disabled,rockchip-mpp"
PACKAGECONFIG[x11] = "-Drkximage=enabled,-Drkximage=disabled,libx11 libdrm"
PACKAGECONFIG[rga] = "-Drga=enabled,-Drga=disabled,rockchip-librga"

S = "${WORKDIR}/git"

inherit meson pkgconfig rockchip_uapi

require recipes-multimedia/gstreamer/gstreamer1.0-plugins-packaging.inc
