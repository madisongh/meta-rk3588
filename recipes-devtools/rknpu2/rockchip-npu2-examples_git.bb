require rockchip-npu2.inc

SRC_URI += "\
    file://0001-OE-builds-of-examples.patch \
    file://0002-yolov5_demo-find-labels-file-with-model.patch \
"

DEPENDS = "stb rockchip-npu2-runtime rockchip-librga"

TARGET_SOC_FOR_MODELS = "UNKNOWN"
TARGET_SOC_FOR_MODELS:rk3588 = "RK3588"
TARGET_SOC_FOR_MODELS:rk3562 = "RK3562"
TARGET_SOC_FOR_MODELS:rk3566 = "RK3566_RK3568"
TARGET_SOC_FOR_MODELS:rk3568 = "RK3566_RK3568"

PACKAGECONFIG ??= "opencv"
PACKAGECONFIG[opencv] = "-DUSE_OPENCV=ON,-DUSE_OPENCV=OFF,opencv"

OECMAKE_SOURCEPATH = "${S}/examples"
EXTRA_OECMAKE = "-DTARGET_SOC=${TARGET_SOC_FOR_MODELS}"

inherit cmake pkgconfig

FILES:${PN} += "${datadir}/rknn"
