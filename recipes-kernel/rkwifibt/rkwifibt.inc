EXTRA_SRC_URI ??= ""
SRC_REPO = "gitlab.com/firefly-linux/external/rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}${EXTRA_SRC_URI}"
SRCBRANCH = "rk3588/firefly"
RKWIFIBT_SRCREV = "d2aa93af6819f3649aefea926e307677208cd781"
SRCREV = "${RKWIFIBT_SRCREV}"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
