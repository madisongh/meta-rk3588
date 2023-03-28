# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit python3native

LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_REPO = "github.com/orangepi-xunlong/u-boot-orangepi.git;protocol=https"
SRCBRANCH = "v2017.09-rk3588"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}; \
           git://github.com/rockchip-linux/rkbin.git;protocol=https;branch=master;name=rkbin;destsuffix=rkbin"
SRCREV = "7103c6a88178f2ed12ef578c49b71a54ec80b4a1"
SRCREV_rkbin = "b0c100f1a260d807df450019774993c761beb79d"
SRCREV_FORMAT = "default_rkbin"

SRC_URI += "\
    file://0001-mach-rockchip-convert-make_fit_atf-script-to-Python3.patch \
    file://0002-make.sh-remove-u-boot-build-steps.patch \
    file://0003-scripts-use-python3-for-mkbootimg.patch \
"

DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native"

# Generate Rockchip style loader binaries
RK_IDBLOCK_IMG = "idblock.img"
RK_LOADER_BIN = "loader.bin"
RK_TRUST_IMG = "trust.img"
UBOOT_BINARY = "uboot.img"

do_compile:append() {
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

		bash -x ./make.sh --spl
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

do_deploy:append() {
	cd ${B}

	for binary in "${RK_IDBLOCK_IMG}" "${RK_LOADER_BIN}" "${RK_TRUST_IMG}";do
		[ -f "${binary}" ] || continue
		install "${binary}" "${DEPLOYDIR}/${binary}-${PV}"
		ln -sf "${binary}-${PV}" "${DEPLOYDIR}/${binary}"
	done
}
