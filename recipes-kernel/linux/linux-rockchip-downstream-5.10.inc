COMPATIBLE_MACHINE = "(rk3588)"

SRCREV = "db1847c8ed8006a212abfd79add346469de4d128"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-5.10"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.110"
PV = "${LINUX_VERSION}+git${SRCPV}"
