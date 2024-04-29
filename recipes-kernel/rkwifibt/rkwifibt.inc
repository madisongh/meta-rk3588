EXTRA_SRC_URI ??= ""
SRC_REPO = "gitlab.com/firefly-linux/external/rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}${EXTRA_SRC_URI}"
SRCBRANCH = "rk3588/firefly"
RKWIFIBT_SRCREV = "2e33895d5eaa7716ed0f83a784f7cc7e120fe543"
SRCREV = "${RKWIFIBT_SRCREV}"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
