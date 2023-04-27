# SPDX-License-Identifier: MIT
DESCRIPTION = "Rockchip Media Process Platform module"
HOMEPAGE = "https://github.com/rockchip-linux/mpp"
LICENSE = "Apache-2.0 & MIT"

LIC_FILES_CHKSUM = "file://inc/rk_mpi.h;beginline=4;endline=14;md5=acbba394ae5639b0c786f60c1f48e3d6 \
                    file://osal/linux/drm.h;beginline=16;endline=33;md5=ead62bafc18fc56add819f3bd30f6886"

SRC_REPO = "github.com/rockchip-linux/mpp.git;protocol=https"
SRCBRANCH = "develop"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "da6ad1ee5e8302f10884d2de626a269fa43c7c9f"

PV = "1.3.8+git${SRCPV}"

S = "${WORKDIR}/git"

inherit pkgconfig cmake rockchip_uapi

EXTRA_OECMAKE = "-DHAVE_DRM=ON"

PACKAGES =+ "${PN}-tests ${PN}-legacy-vpu"
FILES:${PN}-tests = "${bindir}"
FILES:${PN}-legacy-vpu = "${libdir}/librockchip_vpu${SOLIBS}"
