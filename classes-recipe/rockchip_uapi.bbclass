DEPENDS:append:rockchip = " rockchip-uapi-headers"
CFLAGS:prepend:rockchip = "-isystem =${includedir}/rockchip-uapi "
CXXFLAGS:prepend:rockchip = "-isystem =${includedir}/rockchip-uapi "
PACKAGE_ARCH:rockchip = "${RK_PKGARCH}"
