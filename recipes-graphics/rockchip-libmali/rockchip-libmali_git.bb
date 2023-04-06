DESCRIPTION = "Userspace Mali GPU drivers for Rockchip SoCs"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://END_USER_LICENCE_AGREEMENT.txt;md5=3918cc9836ad038c5a090a0280233eea"

SRC_REPO = "gitlab.com/rk3588_linux/linux/libmali.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "309268f7a34ca0bba0ab94a0b09feb0191c77fb8"

PV = "1.9.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

S = "${WORKDIR}/git"

DEPENDS = "coreutils-native libdrm"

PROVIDES:append = " virtual/egl virtual/libgles1 virtual/libgles2 virtual/libgles3 virtual/libgbm"

MALI_GPU ??= "UNKNOWN"
MALI_VERSION ??= "UNKNOWN"
MALI_SUBVERSION ??= "none"
MALI_PLATFORM ??= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', 'gbm', d), d)}"

RDEPENDS:${PN} = " \
	${@ 'wayland' if 'wayland' == d.getVar('MALI_PLATFORM') else ''} \
	${@ 'libx11 libxcb' if 'x11' == d.getVar('MALI_PLATFORM') else ''} \
"

DEPENDS:append = " \
	${@ 'wayland' if 'wayland' == d.getVar('MALI_PLATFORM') else ''} \
	${@ 'libx11 libxcb' if 'x11' == d.getVar('MALI_PLATFORM') else ''} \
"

ASNEEDED = ""

# Inject RPROVIDEs/RCONFLICTs on the generic lib name.
python __anonymous() {
    pn = d.getVar('PN')
    pn_dev = pn + "-dev"
    d.setVar("DEBIAN_NOAUTONAME:" + pn, "1")
    d.setVar("DEBIAN_NOAUTONAME:" + pn_dev, "1")

    for p in (("libegl", "libegl1"),
              ("libgles1", "libglesv1-cm1"),
              ("libgles2", "libglesv2-2"),
              ("libgles3",)):
        pkgs = " " + " ".join(p)
        d.appendVar("RREPLACES:" + pn, pkgs)
        d.appendVar("RPROVIDES:" + pn, pkgs)
        d.appendVar("RCONFLICTS:" + pn, pkgs)

        # For -dev, the first element is both the Debian and original name
        pkgs = " " + p[0] + "-dev"
        d.appendVar("RREPLACES:" + pn_dev, pkgs)
        d.appendVar("RPROVIDES:" + pn_dev, pkgs)
        d.appendVar("RCONFLICTS:" + pn_dev, pkgs)
}

inherit meson pkgconfig

EXTRA_OEMESON = " \
	-Dgpu=${MALI_GPU} \
	-Dversion=${MALI_VERSION} \
	-Dsubversion=${MALI_SUBVERSION} \
	-Dplatform=${MALI_PLATFORM} \
"

do_install:append () {
	if grep -q "\-DMESA_EGL_NO_X11_HEADERS" \
		${D}${libdir}/pkgconfig/egl.pc; then
		sed -i 's/defined(MESA_EGL_NO_X11_HEADERS)/1/' \
			${D}${includedir}/EGL/eglplatform.h
	fi
}

INSANE_SKIP:${PN} = "already-stripped ldflags dev-so textrel"
INSANE_SKIP:${PN}-dev = "staticdev"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

RPROVIDES:${PN}:append = " libmali"

FILES:${PN}-staticdev = ""
FILES:${PN}-dev = " \
	${includedir} \
	${libdir}/lib*.a \
	${libdir}/pkgconfig \
"

# Any remaining files, including .so links for utgard DDK's internal dlopen
FILES:${PN} = "*"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
