COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRCREV = "ff4dd8a6d2ca924f7ddc7f9f01b2a4db76d271bf"
# Bump RK_UAPI_SRCREV only if the Rockchip-specific UAPI headers change
RK_UAPI_SRCREV = "7d3da7bcf26ee3042da7978d3ae1921fe299f2ca"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-2023.12-rockchip-5.10.160"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
BB_GIT_SHALLOW ?= "1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.160"
PV = "${LINUX_VERSION}+git${SRCPV}"
