COMPATIBLE_MACHINE = "(rk3588)"

SRCREV = "39d10004acde3659a5062daa48d961364999718c"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-5.10"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.110"
PV = "${LINUX_VERSION}+git${SRCPV}"
