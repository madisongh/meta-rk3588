require recipes-bsp/u-boot/u-boot-common.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-2024.04-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "bed7e049d3a89c8d5827cfe5b254e5ac5f2d5085"

PV .= "+git${SRCPV}"
