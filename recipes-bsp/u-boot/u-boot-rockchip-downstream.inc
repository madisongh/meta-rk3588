# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit python3native

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}; \
           git://gitlab.com/firefly-linux/rkbin.git;protocol=https;branch=rk3588/firefly;name=rkbin;destsuffix=rkbin"
SRCREV = "fd97f0f106bc5a7fbf57bc44eebe148f5bd0efd4"
SRCREV_rkbin = "43c2ce3ce3abe1163cca58c204f9ead6799634b4"
SRCREV_FORMAT = "default_rkbin"

DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native"
