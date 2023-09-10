# Copyright (c) 2023, M. Madison
# Released under the MIT license (see COPYING.MIT for the terms)

require u-boot-rockchip-downstream-common.inc

SUMMARY = "Universal Boot Loader for embedded devices"
PROVIDES = "virtual/bootloader"

SIGNING_FILES = "\
    file://fitsign.cfg \
"
SRC_URI:append = "${@' ' + d.getVar('SIGNING_FILES') if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) else ''}"

RK_SIGNTOOL_CHIP ?= "${@d.getVar('SOC_FAMILY')[2:]}"
RK_SIGN_BURN_KEY_HASH ?= "0"
# Rockchip special naming here
UBOOT_BINARY = "uboot.img"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "${@bb.utils.contains('UBOOT_ENV_SUFFIX', 'scr', 'u-boot-mkimage-native', '', d)}"

inherit uboot-config uboot-extlinux-config rk-uboot-sign deploy python3native kernel-arch

DEPENDS += "swig-native"
DEPENDS += "bc-native dtc-native coreutils-native ${PYTHON_PN}-pyelftools-native rockchip-rkbin rockchip-rkbin-tools-native"
UBOOT_ASSEMBLE_FITIMAGE_DEPS ?= "${@'virtual/kernel:do_deploy' if d.getVar('UBOOT_SIGN_ENABLE') == '1' else ''}"
RKUSBLOADER_ASSEMBLE_DEPS ?= "${@'virtual/bootloader:do_deploy' if d.getVar('SPL_SIGN_ENABLE') == '1' else ''}"

export RKBIN_TOOLS = "${STAGING_BINDIR_NATIVE}/rkbin-tools"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

PACKAGECONFIG ??= "openssl"
# u-boot will compile its own tools during the build, with specific
# configurations (aka when CONFIG_FIT_SIGNATURE is enabled) openssl is needed as
# a host build dependency.
PACKAGECONFIG[openssl] = ",,openssl-native"

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""

require recipes-bsp/u-boot/u-boot-configure.inc

do_setup_rkbin() {
    cp -Rv ${STAGING_DATADIR}/rkbin/* .
    mkdir tools
    cp -Rv ${STAGING_BINDIR_NATIVE}/rkbin-tools/* tools/
}
do_setup_rkbin[dirs] = "${WORKDIR}/rkbin"
do_setup_rkbin[cleandirs] = "${WORKDIR}/rkbin"

addtask setup_rkbin after do_prepare_recipe_sysroot before do_configure

do_compile() {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
        sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' ${S}/config.mk
    fi

    unset LDFLAGS
    unset CFLAGS
    unset CPPFLAGS

    if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
    then
        echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
        echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
    fi

    if [ -n "${UBOOT_CONFIG}" -o -n "${UBOOT_DELTA_CONFIG}" ]
    then
        unset i j k
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    oe_runmake -C ${S} O=${B}/${config} ${UBOOT_MAKE_TARGET}
                    for binary in ${UBOOT_BINARIES}; do
                        k=$(expr $k + 1);
                        if [ $k -eq $i ]; then
                            cp ${B}/${config}/${binary} ${B}/${config}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX}
                        fi
                    done

                    # Generate the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        oe_runmake -C ${S} O=${B}/${config} u-boot-initial-env
                        cp ${B}/${config}/u-boot-initial-env ${B}/${config}/u-boot-initial-env-${type}
                    fi

                    unset k
                fi
            done
            unset j
        done
        unset i
    else
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}

        # Generate the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            oe_runmake -C ${S} O=${B} u-boot-initial-env
        fi
    fi

    if [ -n "${UBOOT_ENV}" ] && [ "${UBOOT_ENV_SUFFIX}" = "scr" ]
    then
        ${UBOOT_MKIMAGE} -C none -A ${UBOOT_ARCH} -T script -d ${WORKDIR}/${UBOOT_ENV_SRC} ${WORKDIR}/${UBOOT_ENV_BINARY}
    fi
}

# Install DTB to datadir, so that kernel can use it for
# signing, and kernel will deploy UBOOT_DTB_BINARY after signs it.
# Also install SPL DTB to datadir, so we have an unmodified copy
# to use in case the signing task gets re-executed without
# a recompilation.
uboot_sign_install_helper() {
    local b="$1"
    if [ -f "$b/${UBOOT_DTB_BINARY}" ]; then
        # UBOOT_DTB_BINARY is a symlink to UBOOT_DTB_IMAGE, so we
        # need both of them.
        install -Dm 0644 "$b/${UBOOT_DTB_BINARY}" ${D}${datadir}/${UBOOT_DTB_IMAGE}
        ln -sf ${UBOOT_DTB_IMAGE} ${D}${datadir}/${UBOOT_DTB_BINARY}
    else
        bbwarn "${UBOOT_DTB_BINARY} not found in $b"
    fi
    uboot_sign_install_spl_helper "$b" ${D}${datadir}
}

uboot_sign_install_spl_helper() {
    local b="$1"
    local dest="$2"
    if [ -f "$b/${SPL_DIR}/${SPL_DTB_BINARY}" ]; then
        install -Dm 0644 "$b/${SPL_DIR}/${SPL_DTB_BINARY}" $dest/${SPL_DTB_IMAGE}
        ln -sf ${SPL_DTB_IMAGE} $dest/${SPL_DTB_BINARY}
    else
        bbwarn "${SPL_DTB_BINARY} not found in $b"
    fi
}

do_install() {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    # Install the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        install -D -m 644 ${B}/${config}/u-boot-initial-env-${type} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
                    fi
                    if [ "${UBOOT_SIGN_ENABLE}" = "1" ]; then
                        uboot_sign_install_helper ${B}/$config
                    fi
                fi
            done
            unset j
        done
        unset i
    else
        if [ "${UBOOT_SIGN_ENABLE}" = "1" ]; then
	    uboot_sign_install_helper ${B}
        fi
        # Install the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            install -D -m 644 ${B}/u-boot-initial-env ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
        fi
    fi

    if [ -n "${UBOOT_ELF}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        install -m 644 ${B}/${config}/${UBOOT_ELF} ${D}/boot/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${UBOOT_ELF} ${D}/boot/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${D}/boot/${UBOOT_ELF_BINARY}
        fi
    fi

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi

    if [ -n "${SPL_BINARY}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                         install -m 644 ${B}/${config}/${SPL_BINARY} ${D}/boot/${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX}
                         ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}-${type}
                         ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
            ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARYFILE}
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${WORKDIR}/${UBOOT_ENV_BINARY} ${D}/boot/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -Dm 0644 ${UBOOT_EXTLINUX_CONFIG} ${D}/${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi
}

PACKAGE_BEFORE_PN += "${PN}-env ${PN}-extlinux"

RPROVIDES:${PN}-env += "u-boot-default-env"
ALLOW_EMPTY:${PN}-env = "1"
FILES:${PN}-env = " \
    ${@ '${sysconfdir}/${UBOOT_INITIAL_ENV}*' if d.getVar('UBOOT_INITIAL_ENV') else ''} \
    ${sysconfdir}/fw_env.config \
"

FILES:${PN}-extlinux = "${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}"
RDEPENDS:${PN} += "${@bb.utils.contains('UBOOT_EXTLINUX', '1', '${PN}-extlinux', '', d)}"

FILES:${PN} = "/boot ${datadir}"
RDEPENDS:${PN} += "${PN}-env"

do_deploy() {
    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ] ; then
        if [ -e "${UBOOT_DTB_BINARY}" ]; then
	    ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_BINARY}
	    ln -sf ${UBOOT_DTB_IMAGE} ${DEPLOYDIR}/${UBOOT_DTB_SYMLINK}
	fi
    fi

    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${SPL_DTB_BINARY}" ] ; then
        uboot_sign_install_spl_helper ${B} ${DEPLOYDIR}
    fi

    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -D -m 644 ${B}/${config}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}

                    # Deploy the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        install -D -m 644 ${B}/${config}/u-boot-initial-env-${type} ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR}
                        cd ${DEPLOYDIR}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${type}
                    fi
                fi
            done
            unset j
        done
        unset i
    else
        install -D -m 644 ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

        # Deploy the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            install -D -m 644 ${B}/u-boot-initial-env ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}
            cd ${DEPLOYDIR}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${MACHINE}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${UBOOT_INITIAL_ENV}
        fi
    fi

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -D -m 644 ${WORKDIR}/fw_env.config ${DEPLOYDIR}/fw_env.config-${MACHINE}-${PV}-${PR}
        cd ${DEPLOYDIR}
        ln -sf fw_env.config-${MACHINE}-${PV}-${PR} fw_env.config-${MACHINE}
        ln -sf fw_env.config-${MACHINE}-${PV}-${PR} fw_env.config
    fi

    if [ -n "${UBOOT_ELF}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        install -m 644 ${B}/${config}/${UBOOT_ELF} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${UBOOT_ELF} ${DEPLOYDIR}/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
        fi
    fi


     if [ -n "${SPL_BINARY}" ]
     then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        install -m 644 ${B}/${config}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX}
                        rm -f ${DEPLOYDIR}/${SPL_BINARYFILE} ${DEPLOYDIR}/${SPL_SYMLINK}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}-${type}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
            rm -f ${DEPLOYDIR}/${SPL_BINARYNAME} ${DEPLOYDIR}/${SPL_SYMLINK}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARYNAME}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
        fi
    fi


    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${WORKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
        rm -f ${DEPLOYDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -m 644 ${UBOOT_EXTLINUX_CONFIG} ${DEPLOYDIR}/${UBOOT_EXTLINUX_SYMLINK}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}-${MACHINE}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi

    if [ -n "${UBOOT_DTB}" ]
    then
        install -m 644 ${B}/arch/${UBOOT_ARCH}/dts/${UBOOT_DTB_BINARY} ${DEPLOYDIR}/
    fi

    deploy_rockchip_binaries

}

addtask deploy before do_build after do_compile

# Generate Rockchip style loader binaries
RK_IDBLOCK_IMG = "idblock.img"
RK_LOADER_BIN = "loader.bin"
RK_TRUST_IMG = "trust.img"

do_uboot_assemble_fitimage() {
	# Prepare needed files; Rockchip scripts unfortunately assume
	# building in source directory
	for d in make.sh scripts configs arch/arm/mach-rockchip; do
		cp -rT ${S}/${d} ${d}
	done

	# Pick up the u-boot DTB with the RSA public key inserted
	# by the kernel recipe's signing task, and make sure we're
	# using the clean u-boot-spl.dtb staged into the sysroot,
	# rather than one that has the public key inserted
	if [ "${UBOOT_SIGN_ENABLE}" = "1" ]; then
		cp ${DEPLOY_DIR_IMAGE}/${UBOOT_DTB_IMAGE} ${B}/u-boot.dtb
		cp ${SYSROOT_DESTDIR}${datadir}/u-boot-spl.dtb ${B}/spl
		mkdir -p keys
		cp ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.* keys/
	fi

	# uboot.itb (U-Boot proper plus secure OS)
	./make.sh itb
	ln -sf u-boot.itb ${UBOOT_BINARY}
	if ${@'true' if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) and bb.utils.to_boolean(d.getVar('RK_SIGN_BURN_KEY_HASH'), False) else 'false'}; then
		if grep -q '^CONFIG_SPL_FIT_HW_CRYPTO=y' .config ; then
			fdtput -tx spl/u-boot-spl.dtb /signature/key-dev burn-key-hash 0x1
		else
			bberror "burning key hash requires CONFIG_SPL_FIT_HW_CRYPTO=y"
		fi
	fi

	# Reconstruct the SPL binaries in case the DTB has changed
	cat spl/u-boot-spl-nodtb.bin spl/u-boot-spl.dtb > spl/u-boot-spl-dtb.bin
	cp spl/u-boot-spl-dtb.bin spl/u-boot-spl.bin

	# Pack the SPL into the loader after any signing
	./make.sh --spl
	ln -sf *spl_loader*.bin "${RK_LOADER_BIN}"


	# Generate idblock image
	bbnote "${PN}: Generating ${RK_IDBLOCK_IMG} from ${RK_LOADER_BIN}"
	./tools/boot_merger --unpack "${RK_LOADER_BIN}"

	if [ -f FlashHead ]; then
		cat FlashHead FlashData > "${RK_IDBLOCK_IMG}"
	else
		./tools/mkimage -n "${SOC_FAMILY}" -T rksd -d FlashData \
			"${RK_IDBLOCK_IMG}"
	fi

	cat FlashBoot >> "${RK_IDBLOCK_IMG}"

}
do_uboot_assemble_fitimage[dirs] = "${B}"
do_uboot_assemble_fitimage[depends] += "${UBOOT_ASSEMBLE_FITIMAGE_DEPS}"

do_uboot_generate_rsa_keys() {
	if [ "${SPL_SIGN_ENABLE}" = "0" ] && [ "${UBOOT_FIT_GENERATE_KEYS}" = "1" ]; then
		bbwarn "UBOOT_FIT_GENERATE_KEYS is set to 1 eventhough SPL_SIGN_ENABLE is set to 0. The keys will not be generated as they won't be used."
	fi

	if [ "${SPL_SIGN_ENABLE}" = "1" ] && [ "${UBOOT_FIT_GENERATE_KEYS}" = "1" ]; then

		# Generate keys only if they don't already exist
		if [ ! -f "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".key ] || \
			[ ! -f "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".crt ]; then

			echo "Using rk_sign_tool to generate RSA key"
			cp ${RKBIN_TOOLS}/rk_sign_tool ${RKBIN_TOOLS}/setting.ini .
			./rk_sign_tool kk --bits=${UBOOT_FIT_SIGN_NUMBITS} --out .

			# make directory if it does not already exist
			mkdir -p "${SPL_SIGN_KEYDIR}"
			cp private_key.pem ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.key
			cp public_key.pem ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.pubkey

			echo "Generating certificate for signing U-Boot fitImage"
			openssl req ${UBOOT_FIT_KEY_REQ_ARGS} "${UBOOT_FIT_KEY_SIGN_PKCS}" \
				-key "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".key \
				-out "${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}".crt
		fi
	fi

}
do_uboot_generate_rsa_keys[depends] += "rockchip-rkbin-tools-native:do_populate_sysroot"
do_uboot_generate_rsa_keys[dirs] = "${WORKDIR}/gen-rsa-keys"

addtask uboot_generate_rsa_keys before do_uboot_assemble_fitimage after do_unpack

# If you have an external signing server, override this function to
# re-sign your u-boot FIT with your production signing key and sign
# your IDB with the same.
do_rk_sign_bootloader() {
	PATH="${WORKDIR}/rkbin/tools:$PATH"
	if ${@'true' if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) else 'false'}; then
		rk_sign_tool cc --chip ${RK_SIGNTOOL_CHIP}
		rk_sign_tool lk --key ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.key --pubkey ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.pubkey
		if ${@'true' if bb.utils.to_boolean(d.getVar('RK_SIGN_BURN_KEY_HASH'), False) else 'false'}; then
			rk_sign_tool ss --flag 0x20
		fi
		rk_sign_tool sb --idb ${RK_IDBLOCK_IMG}
	fi
}
do_rk_sign_bootloader[dirs] = "${B}"

# If you have an external signing server, override this function to
# sign the USB loader with your production key.
do_rk_sign_usbloader() {
	PATH="${WORKDIR}/rkbin/tools:$PATH"
	if ${@'true' if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) else 'false'}; then
		usbloader=$(basename $(ls -1 ${B}/*_${LOADER_OUTPUT}_*.bin))
		rk_sign_tool cc --chip ${RK_SIGNTOOL_CHIP}
		rk_sign_tool lk --key ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.key --pubkey ${SPL_SIGN_KEYDIR}/${SPL_SIGN_KEYNAME}.pubkey
		if ${@'true' if bb.utils.to_boolean(d.getVar('RK_SIGN_BURN_KEY_HASH'), False) else 'false'}; then
			rk_sign_tool ss --flag 0x20
		fi
		rk_sign_tool sl --loader $usbloader
	fi
}
do_rk_sign_usbloader[dirs] = "${B}"

LOADER_TARGET = "loader"
LOADER_OUTPUT = "spl_loader"
LOADER_TARGET:rk3588 = "--spl"
LOADER_OUTPUT:rk3588 = "download"

do_assemble_rkusbloader() {
	# Prepare needed files; Rockchip scripts unfortunately assume
	# building in source directory
	for d in make.sh scripts configs arch/arm/mach-rockchip; do
		cp -rT ${S}/${d} ${d}
	done
	# If we're signing the U-Boot FIT (not the kernel one), make
	# sure we pick up the SPL DTB that has the public key
	# inserted, otherwise just make sure we've got a clean copy of
	# the SPL with the device tree appended
	if [ "${SPL_SIGN_ENABLE}" = "1" ]; then
		cat spl/u-boot-spl-nodtb.bin ${DEPLOY_DIR_IMAGE}/${SPL_DTB_IMAGE} > spl/u-boot-spl.bin
	else
		cat spl/u-boot-spl-nodtb.bin spl/u-boot-spl.dtb > spl/u-boot-spl.bin
	fi

	./make.sh ${LOADER_TARGET}
}
do_assemble_rkusbloader[dirs] = "${B}"
do_assemble_rkusbloader[depends] += "${RKUSBLOADER_ASSEMBLE_DEPS}"

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
