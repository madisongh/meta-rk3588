require rockchip-librga.inc

SRC_URI += "\
    file://0001-normal-staticly-initialize-the-sina-cosa-table.patch \
    file://0002-normal-remove-multi-task-mode.patch \
"
EXTRA_OEMESON += "-Dlibrga_demo=false"
