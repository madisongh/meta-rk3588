# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

UBOOT_MACHINE = "UNKNOWN"
UBOOT_MACHINE:rk3588 = "rk3588_defconfig"
UBOOT_MACHINE:rk3568 = "rockchip-usbplug_defconfig"

require u-boot-rockchip-downstream.inc

PROVIDES = "rkusbloader"

SRC_URI:append:rk3588 = " file://rk3588-ipc.cfg"
SRC_URI:append:rk3568 = " file://rk3568-usbplug.cfg"

LOADER_TARGET = "loader"
LOADER_OUTPUT = "spl_loader"
LOADER_TARGET:rk3588 = "--spl"
LOADER_OUTPUT:rk3588 = "download"

do_compile:append() {
	cd ${B}
	# Prepare needed files
	for d in make.sh scripts configs arch/arm/mach-rockchip; do
	    cp -rT ${S}/${d} ${d}
	done
	bash -x ./make.sh ${LOADER_TARGET}
}

do_deploy:append() {
	dlfile=$(basename $(ls -1 ${B}/*_${LOADER_OUTPUT}_*.bin))
	install -m 0644 ${B}/$dlfile ${DEPLOYDIR}/$dlfile-${PV}
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/$dlfile
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/download.bin
}
