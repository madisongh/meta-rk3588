# SPDX-License-Identifier: MIT
DESCRIPTION = "Rockchip Media Process Platform module"
HOMEPAGE = "https://github.com/rockchip-linux/mpp"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=cae8403956f8109412d343ffe6e35359"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRC_REPO = "github.com/rockchip-linux/rknpu2.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "4e2101af40a6cbe253eb680673846b7ec832a896"

PV = "1.5.0+git${SRCPV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
