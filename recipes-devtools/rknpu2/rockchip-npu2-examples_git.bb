require rockchip-npu2.inc

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:rk3588 = "rk3588"

SRC_URI += "\
    file://0001-OE-builds-of-examples.patch \
    file://0002-yolov5_demo-find-labels-file-with-model.patch \
"
B = "${WORKDIR}/build"
S = "${WORKDIR}/git"

DEPENDS = "stb rockchip-npu2-runtime rockchip-librga"

PACKAGECONFIG ??= ""
PACKAGECONFIG[opencv] = "-DUSE_OPENCV=ON,-DUSE_OPENCV=OFF,opencv"

OECMAKE_SOURCEPATH = "${S}/examples"
EXTRA_OECMAKE = "-DTARGET_SOC=${@d.getVar('SOC_FAMILY').upper()}"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/rknn"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
