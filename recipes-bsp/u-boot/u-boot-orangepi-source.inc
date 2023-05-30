SRC_REPO = "github.com/orangepi-xunlong/u-boot-orangepi.git;protocol=https"
SRCBRANCH = "v2017.09-rk3588"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "7103c6a88178f2ed12ef578c49b71a54ec80b4a1"

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-orangepi:"

SRC_URI += "\
    file://0001-mach-rockchip-convert-make_fit_atf-script-to-Python3.patch \
    file://0002-make.sh-remove-u-boot-build-steps.patch \
    file://0003-scripts-use-python3-for-mkbootimg.patch \
    file://0004-change-default-boot-order.patch \
"
