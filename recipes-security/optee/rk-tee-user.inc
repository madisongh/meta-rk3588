SRC_REPO = "github.com/madisongh/rockchip-external-security-rk_tee_user.git;protocol=https"
SRCBRANCH = "rk3588-linux-6.1"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "150493459a0c8a57eb7bda49b19b11679ede7f0f"

PV = "3.13.0-rk+git"


COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
