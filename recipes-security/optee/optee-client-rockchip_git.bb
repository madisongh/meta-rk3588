DESCRIPTION = "Pre-built Rockchip OP-TEE client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

SRC_REPO = "gitlab.com/firefly-linux/external/security/bin;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "6053b9e578226933c1472207926dfffcb25299c2"

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

inherit systemd

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
}

do_install() {
    install -d ${D}${sbindir} ${D}${includedir} ${D}${libdir} ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${S}/optee_v2/ta/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
