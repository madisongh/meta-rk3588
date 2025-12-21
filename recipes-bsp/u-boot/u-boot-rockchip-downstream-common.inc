require recipes-bsp/u-boot/u-boot-common.inc

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-2024.04-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "444f633e925b300ee67529ca2c5d6451bc216c66"

PV .= "+git"
