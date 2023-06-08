# Copyright (C) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux/linux-yocto.inc

inherit python3native

require linux-rockchip-downstream-5.10.inc

SRC_URI += "\
    file://systemd.cfg \
    file://platform-configuration.cfg \
    ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
"

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

DEPENDS += "openssl-native lz4-native dtc-native"

set_chosen_bootargs() {
    if [ -z "${RK_KERNEL_ARGS}" ]; then
        return 0
    fi
    for dtb in ${KERNEL_DEVICETREE}; do
        fdtput -t s -p ${B}/arch/${ARCH}/boot/dts/$dtb /chosen bootargs "${RK_KERNEL_ARGS}"
    done
}

do_assemble_fitimage:prepend() {
    set_chosen_bootargs
}

do_assemble_fitimage_initramfs:prepend() {
    set_chosen_bootargs
}
