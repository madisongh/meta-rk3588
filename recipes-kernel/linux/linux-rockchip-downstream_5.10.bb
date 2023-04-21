# Copyright (C) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux/linux-yocto.inc

inherit python3native

COMPATIBLE_MACHINE = "(rk3588)"

SRCREV = "4db5e51d516da43b0b4114a1ec7fb465436ccf96"
SRC_REPO = "github.com/madisongh/linux-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-5.10"
KBRANCH = "${SRCBRANCH}"
SRC_URI = "git://${SRC_REPO};branch=${KBRANCH}"
SRC_URI += "\
    file://systemd.cfg \
    ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "5.10.66"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False), d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-rockchip"
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
