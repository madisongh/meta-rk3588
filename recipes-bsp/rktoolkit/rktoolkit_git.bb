DESCRIPTION = "Rockchip rktoolkit subset"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=919c961282a1817c7f9a6bf495fa7b2e"

SRC_REPO = "gitlab.com/firefly-linux/external/rktoolkit.git;protocol=https"
SRCBRANCH = "rk3588/firefly"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "8ffe98223a3630640651c1376856ca7e1efdbe99"

PV = "1.0+git${SRCPV}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rockchip = "(rockchip)"

RKTOOLKIT_TOOLS ?= "vendor_storage sample_vendor_lib"
CFLAGS += "-Wno-error=format -Wno-error=unused-result"

S = "${WORKDIR}/git"
B = "${S}"

do_compile() {
    oe_runmake ${RKTOOLKIT_TOOLS} CC="${CC} ${CFLAGS} ${LDFLAGS}" PROJECT_DIR="${B}"
}

do_install() {
    for f in ${RKTOOLKIT_TOOLS}; do
        install -D -m 0755 ${B}/$f -t ${D}${bindir}
    done
}
