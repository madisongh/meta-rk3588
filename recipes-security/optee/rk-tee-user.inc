SRC_REPO = "gitlab.com/firefly-linux/external/security/rk_tee_user.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "bad597104b432fce87b0d83feb561d2c62e9b912"

PV = "3.13.0-rk+git${SRCPV}"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
