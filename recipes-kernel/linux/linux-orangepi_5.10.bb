# Copyright (C) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux/linux-yocto.inc

inherit python3native

COMPATIBLE_MACHINE = "(orangepi5)"

SRCREV = "88961a71100e64a97124a674eff8b71863d4cbbc"
SRC_REPO = "github.com/orangepi-xunlong/linux-orangepi.git;protocol=https"
SRCBRANCH = "orange-pi-5.10-rk3588"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
SRC_URI += "\
    file://0001-net-wireless-rtl88x2cs-fix-include-directive.patch \
    file://0002-meta-fix-include-paths-in-driver-makefiles.patch \
    file://0003-scripts-update-shebang-lines-to-use-python3.patch \
    file://cgroups.cfg \
    file://ext4.cfg \
    file://broken-drivers.cfg \
    ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.110"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False), d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-orangepi"
KCONFIG_MODE ?= "--alldefconfig"
KBUILD_DEFCONFIG = "rockchip_linux_defconfig"
SCMVERSION ??= "y"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

DEPENDS += "openssl-native lz4-native"

do_compile() {
    kernel_do_compile
    mv ${B}/*.img ${B}/arch/${ARCH}/boot
}
