inherit rockchip_uapi
PACKAGECONFIG:append:rockchip = " zlib ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gallium', '', d)}"
EXTRA_OEMESON:append:rockchip = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '-Dglx=dri', '', d)}"
PACKAGE_ARCH:rockchip = "${SOC_FAMILY_PKGARCH}"
