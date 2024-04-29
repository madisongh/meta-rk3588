# SPDX-License-Identifier: MIT
DESCRIPTION = "Rockchip Media Process Platform module"
HOMEPAGE = "https://github.com/rockchip-linux/mpp"
LICENSE = "Apache-2.0 & MIT"

LIC_FILES_CHKSUM = "file://inc/rk_mpi.h;beginline=4;endline=14;md5=acbba394ae5639b0c786f60c1f48e3d6 \
                    file://osal/linux/drm.h;beginline=16;endline=33;md5=ead62bafc18fc56add819f3bd30f6886"

SRC_REPO = "github.com/rockchip-linux/mpp.git;protocol=https"
SRCBRANCH = "develop"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "1a4f3d456c2d40c913abc5caccc27370a4b14761"

PV = "1.5.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit pkgconfig cmake rockchip_uapi

PACKAGES =+ "${PN}-tests ${PN}-legacy-vpu"
FILES:${PN}-tests = "${bindir}"
FILES:${PN}-legacy-vpu = "${libdir}/librockchip_vpu${SOLIBS}"
