SRC_REPO = "gitlab.com/firefly-linux/external/rkwifibt.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "rk3588/firefly"
SRCREV = "d2aa93af6819f3649aefea926e307677208cd781"

S = "${WORKDIR}/git"
