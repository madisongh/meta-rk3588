# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require u-boot-rockchip-downstream-common.inc

inherit cml1

DEBUG_BUILD:class-native = "1"

SRC_URI += "file://rk-u-boot-tools.cfg"

SUMMARY = "U-Boot bootloader tools"
DEPENDS += "openssl kern-tools-native"

EXTRA_OEMAKE:class-target = 'CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1 CONFIG_ARCH_ROCKCHIP=1'
EXTRA_OEMAKE:class-native = 'CC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1 CONFIG_ARCH_ROCKCHIP=1'
EXTRA_OEMAKE:class-nativesdk = 'CROSS_COMPILE="${HOST_PREFIX}" CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1 CONFIG_ARCH_ROCKCHIP=1'

RK_TOOLS = "resource_tool fit_info"

do_configure () {
	# Yes, this is crazy. If you build on a system with git < 2.14 from scratch, the tree will
	# be marked as "dirty" and the version will include "-dirty", leading to a reproducibility problem.
	# The issue is the inode count for Licnses/README changing due to do_populate_lic hardlinking a
	# copy of the file. We avoid this by ensuring the index is updated with a "git diff" before the
	# u-boot machinery tries to determine the version.
	#
	# build$ ../git/scripts/setlocalversion ../git
	# ""
	# build$ ln ../git/
	# build$ ln ../git/README ../foo
	# build$ ../git/scripts/setlocalversion ../git
	# ""-dirty
	# (i.e. creating a hardlink dirties the index)
	cd ${S}; git diff; cd ${B}

	oe_runmake -C ${S} sandbox_defconfig O=${B}
	merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
}

do_compile() {
	oe_runmake -C ${S} cross_tools NO_SDL=1 O=${B}
}

do_install () {
	install -d ${D}${bindir}

	# mkimage
	install -m 0755 tools/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage

	# mkenvimage
	install -m 0755 tools/mkenvimage ${D}${bindir}/uboot-mkenvimage
	ln -sf uboot-mkenvimage ${D}${bindir}/mkenvimage

	# dumpimage
	install -m 0755 tools/dumpimage ${D}${bindir}/uboot-dumpimage
	ln -sf uboot-dumpimage ${D}${bindir}/dumpimage

	# fit_check_sign
	install -m 0755 tools/fit_check_sign ${D}${bindir}/uboot-fit_check_sign
	ln -sf uboot-fit_check_sign ${D}${bindir}/fit_check_sign

	for f in ${RK_TOOLS}; do
		install -m 0755 tools/$f ${D}${bindir}/
	done
}

RDEPENDS:${PN} += "dtc rockchip-rkbin-tools"
#RDEPENDS:${PN}:class-native = "dtc-native rockchip-rkbin-native"

BBCLASSEXTEND = "native nativesdk"
