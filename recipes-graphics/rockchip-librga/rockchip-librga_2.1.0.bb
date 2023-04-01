DESCRIPTION = "Rockchip RGA userland API"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=89aea4e17d99a7cacdbeed46a0096b10"
HOMEPAGE = "https://github.com/airockchip/libgra"

SRC_REPO = "github.com/madisongh/rockchip-librga.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "a1b05b8fcf4698176477370fd942b31d9ae66404"

S = "${WORKDIR}/git"

DEPENDS = "libdrm"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dlibdrm=true"
