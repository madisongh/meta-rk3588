# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit python3native

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/orangepi-xunlong/u-boot-orangepi.git;protocol=https"
SRCBRANCH = "v2017.09-rk3588"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}; \
           git://github.com/rockchip-linux/rkbin.git;protocol=https;branch=master;name=rkbin;destsuffix=rkbin"
SRCREV = "7103c6a88178f2ed12ef578c49b71a54ec80b4a1"
SRCREV_rkbin = "b0c100f1a260d807df450019774993c761beb79d"
SRCREV_FORMAT = "default_rkbin"

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-orangepi:"

SRC_URI += "\
    file://0001-mach-rockchip-convert-make_fit_atf-script-to-Python3.patch \
    file://0002-make.sh-remove-u-boot-build-steps.patch \
    file://0003-scripts-use-python3-for-mkbootimg.patch \
"

DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native"
