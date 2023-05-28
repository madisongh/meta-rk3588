# Copyright (c) 2023, M. Madison
# SPDX-License-Identifier: MIT
MACHINEOVERRIDES =. "rockchip:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
RK_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_rockchip"
PACKAGE_EXTRA_ARCHS:append = " ${RK_PKGARCH} ${SOC_FAMILY_PKGARCH}"

KERNEL_IMAGETYPES ?= "Image"
KERNEL_IMAGETYPE ?= "Image"

IMAGE_BOOT_FILES = "${KERNEL_IMAGETYPE} ${@' '.join([os.path.basename(f) + ';rockchip/' + os.path.basename(f) for f in d.getVar('KERNEL_DEVICETREE').split()])}"
IMAGE_FSTYPES ?= "ext4 wic wic.bmap"
WIC_DEPLOY_EXTRA_DEPS ?= "virtual/bootloader:do_deploy"
do_image_wic[depends] += "${WIC_DEPLOY_EXTRA_DEPS}"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "${@'rockchip-udev-rules' if d.getVar('VIRTUAL-RUNTIME_dev_manager') in ['udev', 'systemd'] else ''}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-rockchip-downstream"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-rockchip-downstream"
PREFERRED_PROVIDER_u-boot ?= "u-boot-rockchip-downstream"

PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg-rockchip"
PREFERRED_PROVIDER_virtual/xserver-xf86 ?= "xserver-xorg-rockchip"
PREFERRED_PROVIDER_xserver-xorg ?= "xserver-xorg-rockchip"
XSERVER ?= "xserver-xorg-rockchip \
            xf86-video-modesetting \
            xserver-xorg-rockchip-module-exa \
            ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'xserver-xorg-rockchip-extension-glx', '', d)}"

PREFERRED_PROVIDER_virtual/egl ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libgles1 ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libgles2 ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libgles3 ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libopencl ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libgbm ?= "rockchip-libmali"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa-gl"
PREFERRED_PROVIDER_virtual/mesa ?= "mesa-gl"

# XXX Work around libforms directly depending on mesa rather than virtual/libgl
COMPATIBLE_MACHINE:pn-libforms:rockchip = "(-)"
