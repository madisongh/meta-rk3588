COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRCREV = "5cdab089f1cda7f78abfaa3993be7ef6f6b4d8d1"
# Bump RK_UAPI_SRCREV only if the Rockchip-specific UAPI headers change
RK_UAPI_SRCREV = "cef907463922f246977813d803c440b5b2fb1765"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-6.1.115"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
BB_GIT_SHALLOW ?= "1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "6.1.115"
PV = "${LINUX_VERSION}+git"
