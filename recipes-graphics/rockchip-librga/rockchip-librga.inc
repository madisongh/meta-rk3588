DESCRIPTION = "Rockchip RGA userland API"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=89aea4e17d99a7cacdbeed46a0096b10"
HOMEPAGE = "https://github.com/airockchip/libgra"

SRC_REPO = "github.com/madisongh/rockchip-external-linux-rga.git;protocol=https"
SRCBRANCH = "rk3588-linux-6.1"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Update-meson.build-for-OE-builds.patch \
           "
SRCREV = "396a26666974a4c90e785be93b9b0efd40a941c8"
PE = "1"
PV = "1.10.1-10+git"

S = "${WORKDIR}/git"

DEPENDS = "libdrm"

inherit meson pkgconfig rockchip_uapi

EXTRA_OEMESON = "-Dlibdrm=true"
CXXFLAGS += "-I=${includedir}/rockchip-uapi/drm -I=${includedir}/drm"
CFLAGS += "-I=${includedir}/rockchip-uapi/drm -I=${includedir}/drm"
