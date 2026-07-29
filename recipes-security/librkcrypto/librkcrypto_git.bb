DESCRIPTION = "Rockchip crypto library"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

SRC_REPO = "github.com/madisongh/rockchip-external-security-librkcrypto.git;protocol=https"
SRCBRANCH = "rk3588-linux-6.1"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "df05511fa6c22c02ce1940b03bdc1596bc4e27b9"

SRC_URI += "file://0001-CMakeLists-changes-for-OE-builds.patch"

PV = "1.2.0+git"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

DEPENDS = "optee-client libdrm"

B = "${WORKDIR}/build"

inherit cmake pkgconfig rockchip_uapi

CFLAGS += "-isystem =${includedir}/rockchip-uapi/drm -Wno-array-parameter"

PACKAGES =+ "${PN}-test"
FILES:${PN}-test = "${bindir}"
RDEPENDS:${PN}-test = "${PN}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
