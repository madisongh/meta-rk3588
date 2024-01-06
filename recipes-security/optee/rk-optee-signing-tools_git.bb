DESCRIPTION = "OP-TEE re-signing tools for Rockchip targets"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://v2/tools/ta_resign_tool-release/linux/resign_ta.py;beginline=3;endline=26;md5=89d886bdab0b88b1ed991845e859cc86"

require rk-tee-user.inc

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE:class-native = ""
COMPATIBLE_MACHINE:class-nativesdk = ""

do_configure() {
    :
}

do_compile() {
    # XXX on-the-fly fixup for Python 3
    sed -e "s,#!.*python.*,#!/usr/bin/env python3," \
        -e "s,find(',find(b',g" ${S}/v2/tools/ta_resign_tool-release/linux/resign_ta.py >${B}/resign_ta.py
}

do_install() {
    install -m 0755 -D -t ${D}${bindir} ${B}/resign_ta.py ${S}/v2/tools/change_puk_tool-release/change_puk_linux/change_puk
}

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

RDEPENDS:${PN} += "python3-core python3-netclient python3-pycryptodome"

BBCLASSEXTEND = "native nativesdk"

