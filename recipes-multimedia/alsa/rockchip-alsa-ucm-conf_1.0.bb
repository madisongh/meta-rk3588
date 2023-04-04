DESCRIPTION = "ALSA UCM config additions for Rockchip platforms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

SRC_URI = "\
    file://rockchip-hdmi0 \
    file://rockchip-hdmi1 \
"

do_install() {
    for i in 0 1; do
        install -d ${D}${datadir}/alsa/ucm2/conf.d/rockchip-hdmi$i
        install -m 0644 ${WORKDIR}/rockchip-hdmi$i/* ${D}${datadir}/alsa/ucm2/conf.d/rockchip-hdmi$i/
    done
}

FILES:${PN} = "${datadir}/alsa/ucm2/conf.d"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
