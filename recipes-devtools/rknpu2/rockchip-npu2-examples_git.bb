require rockchip-npu2.inc

SRC_URI += "\
    file://0001-OE-builds-of-examples.patch \
    file://0002-yolov5_demo-find-labels-file-with-model.patch \
"

DEPENDS = "stb rockchip-npu2-runtime rockchip-librga"

PACKAGECONFIG ??= "opencv"
PACKAGECONFIG[opencv] = "-DUSE_OPENCV=ON,-DUSE_OPENCV=OFF,opencv"

OECMAKE_SOURCEPATH = "${S}/examples"
EXTRA_OECMAKE = "-DTARGET_SOC=${@d.getVar('RK_TARGET_SOC').upper()}"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/rknn"
