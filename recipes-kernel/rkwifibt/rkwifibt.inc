EXTRA_SRC_URI ??= ""
SRC_REPO = "github.com/madisongh/rockchip-external-rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}${EXTRA_SRC_URI}"
SRCBRANCH = "rk3588-linux-6.1"
RKWIFIBT_SRCREV = "9d304474b67a8fe5bc03693c2e91db29741e86f8"
SRCREV = "${RKWIFIBT_SRCREV}"
PV .= "+git"

S = "${WORKDIR}/git"
