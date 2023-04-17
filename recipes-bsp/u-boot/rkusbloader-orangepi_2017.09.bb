# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

UBOOT_MACHINE = "rk3588_defconfig"

require u-boot-orangepi.inc

PROVIDES = "rksubloader"

SRC_URI += "file://rk3588-ipc.cfg"

do_compile:append() {
	cd ${B}
	# Prepare needed files
	for d in make.sh scripts configs arch/arm/mach-rockchip; do
	    cp -rT ${S}/${d} ${d}
	done
	bash -x ./make.sh --spl
}

do_deploy:append() {
	dlfile=$(basename $(ls -1 ${B}/*_download_*.bin))
	install -m 0644 ${B}/$dlfile ${DEPLOYDIR}/$dlfile-${PV}
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/$dlfile
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/download.bin
}
