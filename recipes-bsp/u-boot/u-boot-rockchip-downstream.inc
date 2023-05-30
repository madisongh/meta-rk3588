# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit python3native

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "1625bd0638fe77cf0e90209a6bfba2f54a6d5139"

PV .= "+git${SRCPV}"

DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native rockchip-rkbin rockchip-rkbin-tools-native"

export RKBIN_TOOLS = "${STAGING_BINDIR_NATIVE}/rkbin-tools"

do_setup_rkbin() {
    cp -Rv ${STAGING_DATADIR}/rkbin/* .
    mkdir tools
    cp -Rv ${STAGING_BINDIR_NATIVE}/rkbin-tools/* tools/
}
do_setup_rkbin[dirs] = "${WORKDIR}/rkbin"
do_setup_rkbin[cleandirs] = "${WORKDIR}/rkbin"

addtask setup_rkbin after do_prepare_recipe_sysroot before do_configure
