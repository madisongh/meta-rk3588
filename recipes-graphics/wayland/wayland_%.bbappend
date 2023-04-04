do_install:append:rk3588() {
    rm -rf ${D}/${includedir}/wayland-egl*.h
    rm -rf ${D}/${libdir}/libwayland-egl*
    rm -rf ${D}/${libdir}/pkgconfig/wayland-egl*
}

PACKAGE_ARCH:rk3588 = "${SOC_FAMILY_PKGARCH}"
