COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRCREV = "41686f3616521eab5c5d0d8d2b9ed9f0dc22c432"
# Bump RK_UAPI_SRCREV only if the Rockchip-specific UAPI headers change
RK_UAPI_SRCREV = "41686f3616521eab5c5d0d8d2b9ed9f0dc22c432"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-5.10.160"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
BB_GIT_SHALLOW ?= "1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.160"
PV = "${LINUX_VERSION}+git${SRCPV}"
