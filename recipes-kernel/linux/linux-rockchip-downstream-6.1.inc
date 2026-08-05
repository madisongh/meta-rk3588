COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRCREV = "e64fa779ce7816a2fe4c0e2cbc9debcc8a98d448"
# Bump RK_UAPI_SRCREV only if the Rockchip-specific UAPI headers change
RK_UAPI_SRCREV = "b3ae428291ceab9dbe0effbfd21ac888b5f8b999"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-6.1.118"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
BB_GIT_SHALLOW ?= "1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "6.1.118"
PV = "${LINUX_VERSION}+git"
