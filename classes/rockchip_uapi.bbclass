DEPENDS:append:rk3588 = " rockchip-uapi-headers"
CFLAGS:prepend:rk3588 = "-isystem =${includedir}/rockchip-uapi "
PACKAGE_ARCH:rk3588 = "${SOC_FAMILY_PKGARCH}"
