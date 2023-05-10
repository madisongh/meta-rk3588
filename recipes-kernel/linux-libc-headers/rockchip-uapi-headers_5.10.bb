# Copyright (C) 2021, Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux-libc-headers/linux-libc-headers.inc
require recipes-kernel/linux/linux-rockchip-downstream-5.10.inc

DEPENDS += "linux-libc-headers"

S = "${WORKDIR}/git"

HEADER_OVERRIDES = "\
    drm/drm_fourcc.h \
    linux/dma-buf.h \
    linux/dma-heap.h \
    linux/fec-config.h \
    linux/media-bus-format.h \
    linux/serial_reg.h \
    linux/usb/g_uvc.h \
    linux/usb/video.h \
    linux/v4l2-controls.h \
    linux/videodev2.h \
"

do_install_prestage() {
    oe_runmake headers_install INSTALL_HDR_PATH=${WORKDIR}/prestage
    # Kernel should not be exporting this header
    rm -f ${WORKDIR}/prestage/include/scsi/scsi.h

    # The ..install.cmd conflicts between various configure runs
    find ${WORKDIR}/prestage -name ..install.cmd | xargs rm -f
}
do_install_prestage[cleandirs] = "${WORKDIR}/prestage"
do_install_prestage[dirs] = "${WORKDIR}/prestage ${B}"

python do_cleanse_prestage() {
    overrides = d.getVar('HEADER_OVERRIDES').split()
    sysroot_include = d.getVar('STAGING_INCDIR')
    for root, _, files in os.walk("."):
        for hdr in files:
            hdrpath = os.path.normpath(os.path.join(root, hdr))
            if hdrpath in overrides:
                bb.note("Override: {}".format(hdrpath))
                continue
            if os.path.exists(os.path.join(sysroot_include, hdrpath)):
                os.unlink(hdrpath)
                continue
            bb.note("Keeping:  {}".format(hdrpath))
}
do_cleanse_prestage[dirs] = "${WORKDIR}/prestage/include"

do_install() {
    install -d ${D}${includedir}/rockchip-uapi/include
    cp -R --preserve=mode,timestamps,links ${WORKDIR}/prestage/include/* ${D}${includedir}/rockchip-uapi/
    find ${D}${includedir}/rockchip-uapi -depth -type d -empty -exec rmdir {} \;
}
do_install_armmultilib() {
    :
}

addtask install_prestage after do_compile before do_install
addtask cleanse_prestage after do_install_prestage before do_install

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
