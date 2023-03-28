require rockchip-npu2.inc

SRC_URI += "file://rknn-server.service.in"
SRC_URI += "file://rknn-server.init.in"

RUNTIME_SUBDIR = ""
RUNTIME_SUBDIR:rk3588 = "runtime/RK3588/Linux"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "rk3588"

B = "${WORKDIR}/build"

inherit systemd update-rc.d

do_configure() {
    :
}

do_compile() {
   for f in rknn-server.service rknn-server.init; do
      sed -e"s!@BINDIR@!${bindir}!" -e"s!@BASE_BINDIR@!${base_bindir}!" ${WORKDIR}/$f.in > ${B}/$f
   done
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/${RUNTIME_SUBDIR}/rknn_server/aarch64/usr/bin/rknn_server ${D}${bindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${B}/rknn-server.init ${D}${sysconfdir}/init.d/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/rknn-server.service ${D}${systemd_system_unitdir}/
    install -d ${D}${includedir}
    install -m 0644 ${S}/${RUNTIME_SUBDIR}/librknn_api/include/rknn_api.h ${D}${includedir}/
    install -d ${D}${libdir}
    install -m 0644 ${S}/${RUNTIME_SUBDIR}/librknn_api/aarch64/librknnrt.so ${D}${libdir}/
    ln -sf librknnrt.so ${D}${libdir}/librknn_api.so
    # Needed for the example programs
    install -m 0644 ${S}/examples/3rdparty/rk_mpi_mmz/lib/Linux/aarch64/libmpimmz.so ${D}${libdir}/
    install -m 0644 ${S}/examples/3rdparty/rk_mpi_mmz/include/rk_mpi_mmz.h ${D}${includedir}/
}

INITSCRIPT_NAME = "rknn-server"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "rknn-server.service"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP:${PN} = "dev-so ldflags"
FILES_SOLIBSDEV = ""
FILES:${PN}-dev = "${includedir} ${libdir}/librknn_api.so"
FILES:${PN} += "${libdir}/librknnrt.so ${libdir}/libmpimmz.so"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
