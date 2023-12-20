EXTRA_SRC_URI ??= ""
SRC_REPO = "gitlab.com/firefly-linux/external/rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}${EXTRA_SRC_URI}"
SRCBRANCH = "rk3588/firefly"
RKWIFIBT_SRCREV = "aab2a823444f5dbbe6c2de00b2a05b7effad63f9"
SRCREV = "${RKWIFIBT_SRCREV}"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
