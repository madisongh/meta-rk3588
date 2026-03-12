require rockchip-librga.inc
DESCRIPTION = "Rockchip RGA userland API - demo"
DEPENDS += "rockchip-librga"
EXTRA_OEMESON += "-Dlibrga_demo=true"

SRC_URI += "\
    file://0001-Fix-DMA-heap-and-local-file-paths.patch \
    file://0002-Fix-dlopen-for-libdrm-to-use-full-soname.patch \
"

do_install:append() {
    install -D -m 0644 -t ${D}${datadir}/${BPN} ${S}/samples/sample_file/*.bin
}

FILES:${PN} += "${datadir}/${BPN}"
