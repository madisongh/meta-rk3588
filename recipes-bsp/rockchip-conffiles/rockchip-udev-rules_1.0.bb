DESCRIPTION = "udev rules for Rockchip SoCs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://rockchip-udev.rules"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

do_install() {
    if [ -s ${WORKDIR}/rockchip-udev.rules ]; then
        install -d ${D}${nonarch_base_libdir}/udev/rules.d
        install -m 0644 ${WORKDIR}/rockchip-udev.rules ${D}${nonarch_base_libdir}/udev/rules.d/99-rockchip.rules
    fi
}
ALLOW_EMPTY:${PN} = "1"
FILES:${PN} = "${nonarch_base_libdir}/udev/rules.d"
PACKAGE_ARCH = "${MACHINE_ARCH}"
