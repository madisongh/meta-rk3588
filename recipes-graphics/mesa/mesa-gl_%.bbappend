PACKAGECONFIG:class-target:append:rk3588 = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'dri3 gallium', 'osmesa', d)}"
EXTRA_OEMESON:append:rk3588 = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '-Dglx=dri', '', d)}"
PACKAGE_ARCH:rk3588 = "${SOC_FAMILY_PKGARCH}"
