inherit rockchip_uapi
PACKAGECONFIG:class-target:append:rockchip = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'dri3 gallium', 'osmesa', d)}"
EXTRA_OEMESON:append:rockchip = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '-Dglx=dri', '', d)}"
PACKAGE_ARCH:rockchip = "${SOC_FAMILY_PKGARCH}"
