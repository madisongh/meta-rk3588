SRC_REPO = "gitlab.com/Firefly-Linux/external/rkwifibt.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "cb88ff30aac518ca5e6eb66a100b8158bf34402b"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "(rk3588)"

PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
