DESCRIPTION = "OP-TEE client headers for Rockhip's prebuilt client"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://v2/client_export/public/tee_plugin_method.h;endline=1;md5=999e469d517ea75c6012ac6639dce016"

require rk-tee-user.inc

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/v2/client_export/public/*.h ${D}${includedir}/
}

ALLOW_EMPTY:${PN} = "1"
