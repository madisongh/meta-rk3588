DESCRIPTION = "Rockchip RGA userland API"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=89aea4e17d99a7cacdbeed46a0096b10"
HOMEPAGE = "https://github.com/airockchip/libgra"

SRC_REPO = "gitlab.com/firefly-linux/external/linux-rga.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "c4347cd97c638f3a938ab2fa969798d7ab65cd5b"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "libdrm"

inherit meson pkgconfig rockchip_uapi

EXTRA_OEMESON = "-Dlibdrm=true"
