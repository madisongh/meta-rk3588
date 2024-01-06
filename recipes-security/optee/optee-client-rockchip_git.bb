DESCRIPTION = "Pre-built Rockchip OP-TEE client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

SRC_REPO = "gitlab.com/firefly-linux/external/security/bin.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "61902b39cb5b266cd0bb9675e768e07d6ff1baca"

SRC_URI += "\
    file://tee-supplicant.service.in \
    file://tee-supplicant.sh.in \
"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PV = "3.13.0-rk+git${SRCPV}"

PROVIDES = "optee-client"

DEPENDS = "optee-client-rockchip-headers"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit systemd rk-optee-ta-signing

do_configure() {
    :
}

do_compile() {
    sed -e's,@sbindir@,${sbindir},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${WORKDIR}/tee-supplicant.service.in >${B}/tee-supplicant.service
    sed -e's,@sbindir@,${sbindir},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        -e's,@stripped_path@,${base_sbindir}:${base_bindir}:${sbindir}:${bindir},g' \
        ${WORKDIR}/tee-supplicant.sh.in >${B}/tee-supplicant.sh
    rm -rf ${B}/ta
    cp -R ${S}/optee_v2/ta ${B}/
}

do_install() {
    install -d ${D}${sbindir} ${D}${includedir} ${D}${libdir} ${D}${nonarch_base_libdir}/optee_armtz
     find ${B}/ta -type f -name '*.ta' | while read inf; do
        outf=$(basename $inf)
        install -m 0644 $inf ${D}${nonarch_base_libdir}/optee_armtz/$outf
    done
    install -m 0644 ${S}/optee_v2/include/*.h ${D}${includedir}
    install -m 0755 ${S}/optee_v2/lib/arm64/tee-supplicant ${D}${sbindir}/
    install -m 0644 ${S}/optee_v2/lib/arm64/libteec.so.1.0.0 ${D}${libdir}/
    ln -s libteec.so.1.0.0 ${D}${libdir}/libteec.so.1
    ln -s libteec.so.1.0.0 ${D}${libdir}/libteec.so
    install -m 0644 ${S}/optee_v2/lib/arm64/libckteec.so.0.1.0 ${D}${libdir}/
    ln -s libteec.so.1.0.0 ${D}${libdir}/libckteec.so.0
    ln -s libteec.so.1.0.0 ${D}${libdir}/libckteec.so
    install -m 0644 ${S}/optee_v2/lib/arm64/librk_tee_service.so ${D}${libdir}/
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${B}/tee-supplicant.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${B}/tee-supplicant.sh ${D}${sysconfdir}/init.d/
}

SYSTEMD_SERVICE:${PN} = "tee-supplicant.service"
FILES_SOLIBSDEV = "${libdir}/libteec${SOLIBSDEV} ${libdir}/libckteec${SOLIBSDEV}"
FILES:${PN} += "${nonarch_base_libdir}/optee_armtz ${libdir}/"
INSANE_SKIP:${PN} = "ldflags"
# Wrong SONAME used in librk_tee_service.so
RPROVIDES:${PN} += "optee-client libteec.so.1.0()(64bit)"
RDEPENDS:${PN} += "optee-client-rockchip-headers"

