FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:rockchip = " file://0001-HACK-egl-Prefer-using-libmali.so.1-s-Wayland-EGL-API.patch "
PACKAGE_ARCH:rockchip = "${RK_PKGARCH}"
