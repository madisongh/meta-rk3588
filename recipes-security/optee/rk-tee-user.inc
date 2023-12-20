SRC_REPO = "gitlab.com/firefly-linux/external/security/rk_tee_user.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "6f741be69106e02d45566522affbdc9f3ed4bd36"

PV = "3.13.0-rk+git${SRCPV}"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
