DESCRIPTION = "OP-TEE client headers for Rockhip's prebuilt client"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://client_export/public/tee_plugin_method.h;endline=1;md5=999e469d517ea75c6012ac6639dce016"

require rk-tee-user.inc

PROVIDES = "optee-test"

DEPENDS = "optee-tadevkit-rockchip optee-client-rockchip python3-pyelftools-native python3-cryptography-native python3-pycryptodome-native"

inherit python3native

export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

S = "${WORKDIR}/git/v2"
B = "${WORKDIR}/build"

EXTRA_OEMAKE = "\
    q= V=1 O=${B} BUILD_CA=y TA_DEV_KIT_DIR=${STAGING_INCDIR}/optee/export-user_ta \
    CROSS_COMPILE=${HOST_PREFIX} \
    OPTEE_CLIENT_LIB=${STAGING_LIBDIR} \
"

do_compile() {
    oe_runmake -C ${S}/ta CFLAGS="${TOOLCHAIN_OPTIONS}" ta
    oe_runmake -C ${S}/host/xtest CFLAGS64="${TOOLCHAIN_OPTIONS}" TA_DIR="${nonarch_base_libdir}/optee_armtz" xtest
    oe_runmake -C ${S}/host/supp_plugin CFG_TEE_PLUGIN_LOAD_PATH="${libdir}/tee-supplicant/plugins"
    oe_runmake -C ${S}/host/rk_test rk_test
}

do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    find ${B} -name '*.ta' -exec install -m 0644 {} ${D}${nonarch_base_libdir}/optee_armtz/ \;
    install -d ${D}${bindir}
    install -m 0755 ${B}/xtest/xtest ${B}/rktest ${D}${bindir}
    install -d ${D}${libdir}/tee-supplicant/plugins
    install -m 0644 ${B}/supp_plugin/*.plugin ${D}${libdir}/tee-supplicant/plugins/
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz ${libdir}/tee-supplicant/plugins"
RDEPENDS:${PN} = "optee-client-rockchip"
RPROVIDES:${PN} += "optee-test"
