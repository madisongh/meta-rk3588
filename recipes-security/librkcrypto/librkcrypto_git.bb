DESCRIPTION = "Rockchip crypto library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

SRC_REPO = "gitlab.com/firefly-linux/external/security/librkcrypto.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "70030e714459a2f2c3742869dd24fe788edd1979"

SRC_URI += "file://0001-CMakeLists-changes-for-OE-builds.patch"

PV = "1.2.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

DEPENDS = "optee-client libdrm"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit cmake pkgconfig rockchip_uapi

CFLAGS += "-isystem =${includedir}/rockchip-uapi/drm -Wno-array-parameter"

PACKAGES =+ "${PN}-test"
FILES:${PN}-test = "${bindir}"
RDEPENDS:${PN}-test = "${PN}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
