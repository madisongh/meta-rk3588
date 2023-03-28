# SPDX-License-Identifier: MIT
DESCRIPTION = "Rockchip Media Process Platform module"
HOMEPAGE = "https://github.com/rockchip-linux/mpp"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=cae8403956f8109412d343ffe6e35359"

SRC_REPO = "github.com/rockchip-linux/rknpu2.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "ef74cafa8013ffafe6d932f38ce2ea26d37a0283"

PV = "1.4.0+git${SRCPV}"

S = "${WORKDIR}/git"
