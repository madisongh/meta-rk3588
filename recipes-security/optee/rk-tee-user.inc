SRC_REPO = "gitlab.com/firefly-linux/external/security/rk_tee_user.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "b266e9a7348fc0856416e6aebc43a63f2dae8bf4"

PV = "3.13.0-rk+git${SRCPV}"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
