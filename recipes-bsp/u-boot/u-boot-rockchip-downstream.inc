# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit python3native

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/madisongh/u-boot-rockchip-downstream.git;protocol=https"
SRCBRANCH = "patches-rockchip-v2017.09"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "1625bd0638fe77cf0e90209a6bfba2f54a6d5139"

PV .= "+git${SRCPV}"

DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native rockchip-rkbin rockchip-rkbin-tools-native"

export RKBIN_TOOLS = "${STAGING_BINDIR_NATIVE}/rkbin-tools"

do_setup_rkbin() {
    cp -Rv ${STAGING_DATADIR}/rkbin/* .
    mkdir tools
    cp -Rv ${STAGING_BINDIR_NATIVE}/rkbin-tools/* tools/
}
do_setup_rkbin[dirs] = "${WORKDIR}/rkbin"
do_setup_rkbin[cleandirs] = "${WORKDIR}/rkbin"

addtask setup_rkbin after do_prepare_recipe_sysroot before do_configure

# Generate Rockchip style loader binaries
RK_IDBLOCK_IMG = "idblock.img"
RK_LOADER_BIN = "loader.bin"
RK_TRUST_IMG = "trust.img"
UBOOT_BINARY = "uboot.img"

make_rockchip_binaries() {
	cd ${B}
	if [ -e "${B}/prebuilt/${UBOOT_BINARY}" ]; then
		bbnote "${PN}: Using prebuilt images."
		ln -sf ${B}/prebuilt/*.bin ${B}/prebuilt/*.img ${B}/
		ln -sf *_loader*.bin "${RK_LOADER_BIN}"
	else
		# Prepare needed files
		for d in make.sh scripts configs arch/arm/mach-rockchip; do
			cp -rT ${S}/${d} ${d}
		done

		./make.sh --spl
		ln -sf *spl_loader*.bin "${RK_LOADER_BIN}"
		# uboot.itb (U-Boot proper plus secure OS)
		./make.sh itb
		ln -sf u-boot.itb "${UBOOT_BINARY}"
	fi


	# Generate idblock image
	bbnote "${PN}: Generating ${RK_IDBLOCK_IMG} from ${RK_LOADER_BIN}"
	./tools/boot_merger --unpack "${RK_LOADER_BIN}"

	if [ -f FlashHead ];then
		cat FlashHead FlashData > "${RK_IDBLOCK_IMG}"
	else
		./tools/mkimage -n "${SOC_FAMILY}" -T rksd -d FlashData \
			"${RK_IDBLOCK_IMG}"
	fi

	cat FlashBoot >> "${RK_IDBLOCK_IMG}"
}

LOADER_TARGET = "loader"
LOADER_OUTPUT = "spl_loader"
LOADER_TARGET:rk3588 = "--spl"
LOADER_OUTPUT:rk3588 = "download"

make_rockchip_loader() {
	cd ${B}
	# Prepare needed files
	for d in make.sh scripts configs arch/arm/mach-rockchip; do
	    cp -rT ${S}/${d} ${d}
	done
	bash -x ./make.sh ${LOADER_TARGET}
}

deploy_rockchip_binaries() {
	cd ${B}

	for binary in "${RK_IDBLOCK_IMG}" "${RK_LOADER_BIN}" "${RK_TRUST_IMG}";do
		[ -f "${binary}" ] || continue
		install "${binary}" "${DEPLOYDIR}/${binary}-${PV}"
		ln -sf "${binary}-${PV}" "${DEPLOYDIR}/${binary}"
	done
}

deploy_rockchip_loader() {
	dlfile=$(basename $(ls -1 ${B}/*_${LOADER_OUTPUT}_*.bin))
	install -m 0644 ${B}/$dlfile ${DEPLOYDIR}/$dlfile-${PV}
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/$dlfile
	ln -sf $dlfile-${PV} ${DEPLOYDIR}/download.bin
}
