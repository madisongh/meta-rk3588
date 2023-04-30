DESCRIPTION = "Rockchip RGA userland API"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=89aea4e17d99a7cacdbeed46a0096b10"
HOMEPAGE = "https://github.com/airockchip/libgra"

SRC_REPO = "github.com/madisongh/rockchip-librga.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "7548e39c1944e37f34d110b01f83c9a707b4e6db"

S = "${WORKDIR}/git"

DEPENDS = "libdrm"

inherit meson pkgconfig rockchip_uapi

EXTRA_OEMESON = "-Dlibdrm=true"
