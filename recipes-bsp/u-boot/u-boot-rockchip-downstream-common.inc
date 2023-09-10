require recipes-bsp/u-boot/u-boot-common.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-2023.07-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "46b6311c6f3d7a0a67da33e9b62f4823bd6437ea"

PV .= "+git${SRCPV}"
