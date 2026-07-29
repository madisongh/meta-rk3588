EXTRA_SRC_URI ??= ""
SRC_REPO = "github.com/madisongh/rockchip-external-rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}${EXTRA_SRC_URI}"
SRCBRANCH = "rk3588-linux-6.1"
RKWIFIBT_SRCREV = "f88c9daa0334b329ccd8d6a52e4b64d4ae6bde02"
SRCREV = "${RKWIFIBT_SRCREV}"
PV .= "+git"

