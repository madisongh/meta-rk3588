DESCRIPTION = "ALSA UCM config additions for Rockchip platforms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

SRC_URI = "\
    file://rockchip-hdmi0 \
    file://rockchip-hdmi1 \
    file://rockchip-es8388 \
"

do_install() {
    for i in 0 1; do
        install -d ${D}${datadir}/alsa/ucm2/conf.d/rockchip-hdmi$i
        install -m 0644 ${WORKDIR}/rockchip-hdmi$i/* ${D}${datadir}/alsa/ucm2/conf.d/rockchip-hdmi$i/
    done
    install -D -t ${D}${datadir}/alsa/ucm2/conf.d/rockchip-es8388/ -m 0644 ${WORKDIR}/rockchip-es8388/*
}

FILES:${PN} = "${datadir}/alsa/ucm2/conf.d"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
