DEPENDS:append:rockchip = " rockchip-uapi-headers"
CFLAGS:prepend:rockchip = "-isystem =${includedir}/rockchip-uapi "
PACKAGE_ARCH:rockchip = "${SOC_FAMILY_PKGARCH}"
