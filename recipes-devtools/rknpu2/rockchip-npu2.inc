# SPDX-License-Identifier: MIT
DESCRIPTION = "Rockchip Neural Network processor runtime"
HOMEPAGE = "https://github.com/airockchip"
LICENSE = "Proprietary"

LIC_FILES_CHKSUM = "file://rknpu2/runtime/Linux/librknn_api/include/rknn_api.h;beginline=5;endline=9;md5=39d12ff40318c74dfc09ccf365eff5de"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRC_REPO = "github.com/airockchip/rknn-toolkit2.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "a8dd54d41e92c95b4f95780ed0534362b2c98b92"

PV = "2.3.0"

B = "${WORKDIR}/build"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
