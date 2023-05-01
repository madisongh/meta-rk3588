COMPATIBLE_MACHINE = "(rk3588)"

SRCREV = "af9aa51bedf52a20c32ee9e7125516331d9e87b9"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-5.10"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
BB_GIT_SHALLOW ?= "1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.110"
PV = "${LINUX_VERSION}+git${SRCPV}"
