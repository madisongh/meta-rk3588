# Copyright (c) 2023, M. Madison
# SPDX-License-Identifier: MIT
SOC_FAMILY = "rk3588"
require conf/machine/include/soc-family.inc
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
PACKAGE_EXTRA_ARCHS:append = " ${SOC_FAMILY_PKGARCH}"
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/armv8-2a/tune-cortexa76-cortexa55.inc

SERIAL_CONSOLES ?= "1500000;ttyFIQ0"

KERNEL_IMAGETYPE = "Image"

UBOOT_MACHINE = "rk3588_defconfig"

MACHINE_FEATURES = "alsa ext2 ext3 usbhost"
IMAGE_BOOT_FILES = "${KERNEL_IMAGETYPE} ${@' '.join([os.path.basename(f) + ';rockchip/' + os.path.basename(f) for f in d.getVar('KERNEL_DEVICETREE').split()])}"
IMAGE_FSTYPES ?= "ext4 wic wic.bmap"
WIC_DEPLOY_EXTRA_DEPS ?= "virtual/bootloader:do_deploy"
do_image_wic[depends] += "${WIC_DEPLOY_EXTRA_DEPS}"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "${@'rockchip-udev-rules' if d.getVar('VIRTUAL-RUNTIME_dev_manager') in ['udev', 'systemd'] else ''}"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg-rockchip"
PREFERRED_PROVIDER_virtual/xserver-xf86 = "xserver-xorg-rockchip"
PREFERRED_PROVIDER_xserver-xorg = "xserver-xorg-rockchip"
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

MALI_GPU = "valhall-g610"
MALI_VERSION = "g6p0"

# XXX Work around libforms directly depending on mesa rather than virtual/libgl
COMPATIBLE_MACHINE:pn-libforms:rk3588 = "(-)"