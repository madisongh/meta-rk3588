do_install:append:rockchip() {
    rm -rf ${D}/${includedir}/wayland-egl*.h
    rm -rf ${D}/${libdir}/libwayland-egl*
    rm -rf ${D}/${libdir}/pkgconfig/wayland-egl*
}

PACKAGE_ARCH:rockchip = "${SOC_FAMILY_PKGARCH}"
