DESCRIPTION = "OP-TEE TA dev kit for prebuilt Rockchip"
LICENSE = "BSD-2-Clause & Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "\
    file://v2/export-ta_arm64/include/tee_api.h;endline=1;md5=999e469d517ea75c6012ac6639dce016 \
    file://v2/export-ta_arm64/include/mbedtls/version.h;beginline=7;endline=23;md5=2a5d489018f1165514f494b3e2a1e8e0 \
    file://v2/export-ta_arm64/include/libcxx/include/version;beginline=2;endline=9;md5=2cbc6a4faaa6554ca479aa40d8ce9c90 \
"

require rk-tee-user.inc

PROVIDES = "optee-os-tadevkit"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}/optee/export-user_ta
    cp -R --preserve=mode,timestamps ${S}/v2/export-ta_arm64/* ${D}${includedir}/optee/export-user_ta/
    sed -i -e's,-mstrict-align,,' -e's,-mno-outline-atomics,,' ${D}${includedir}/optee/export-user_ta/mk/conf.mk
    install -D -m 0644 ${S}/v2/ta/rk_public_api/*.[ch] -t ${D}${includedir}/optee/export-user_ta/rk_public_api
}

FILES:${PN} = "${includedir}/optee"
INSANE_SKIP:${PN}-dev = "staticdev"
