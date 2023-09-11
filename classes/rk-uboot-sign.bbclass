# This file is part of U-Boot verified boot support and is intended to be
# inherited from u-boot recipe and from kernel-fitimage.bbclass.
#
# The signature procedure requires the user to generate an RSA key and
# certificate in a directory and to define the following variable:
#
#   UBOOT_SIGN_KEYDIR = "/keys/directory"
#   UBOOT_SIGN_KEYNAME = "dev" # keys name in keydir (eg. "dev.crt", "dev.key")
#   UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
#   UBOOT_SIGN_ENABLE = "1"
#
# As verified boot depends on fitImage generation, following is also required:
#
#   KERNEL_CLASSES ?= " kernel-fitimage "
#   KERNEL_IMAGETYPE ?= "fitImage"
#
# The signature support is limited to the use of CONFIG_OF_SEPARATE in U-Boot.
#
# The tasks sequence is set as below, using DEPLOY_IMAGE_DIR as common place to
# treat the device tree blob:
#
# * The u-boot:do_install:append from the OE-Core uboot-sign bbclass
#   is not present here; the logic there has been added to the u-boot recipe.
#
# * virtual/kernel:do_assemble_fitimage
#   Sign the image
#
# * u-boot:do_deploy[postfuncs]
#   Deploy files like UBOOT_DTB_IMAGE, UBOOT_DTB_SYMLINK and others.
#
# For more details on signature process, please refer to U-Boot
# documentation.

UBOOT_MKIMAGE_SIGN_ARGS ?= ""

# We need some variables from u-boot-config
inherit uboot-config

# Enable use of a U-Boot fitImage
UBOOT_FITIMAGE_ENABLE ?= "1"

# Check RK_SECURE_BOOT when you need to determine if signing is being
# used.
RK_SECURE_BOOT ??= "0"
# These next two variables drive whether the signing happens directly
# during the build or not.  If you use an external signing service,
# set these to 0.
UBOOT_SIGN_ENABLE ?= "${@'1' if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) else '0'}"
SPL_SIGN_ENABLE ?= "${@'1' if bb.utils.to_boolean(d.getVar('RK_SECURE_BOOT'), False) else '0'}"

# Rockchip scripts assume 'dev' is the key name, and should be used
# for both the U-Boot FIT and the kernel FIT.
SPL_SIGN_KEYNAME ?= "dev"
UBOOT_SIGN_KEYNAME ?= "${SPL_SIGN_KEYNAME}"
SPL_SIGN_KEYDIR ?= "${TOPDIR}/signing-keys"
UBOOT_SIGN_KEYDIR ?= "${SPL_SIGN_KEYDIR}"

# Default value for deployment filenames.
UBOOT_DTB_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.dtb"
UBOOT_DTB_BINARY ?= "u-boot.dtb"
UBOOT_DTB_SYMLINK ?= "u-boot-${MACHINE}.dtb"
UBOOT_NODTB_IMAGE ?= "u-boot-nodtb-${MACHINE}-${PV}-${PR}.bin"
UBOOT_NODTB_BINARY ?= "u-boot-nodtb.bin"
UBOOT_NODTB_SYMLINK ?= "u-boot-nodtb-${MACHINE}.bin"
UBOOT_ITS_IMAGE ?= "u-boot-its-${MACHINE}-${PV}-${PR}"
UBOOT_ITS ?= "u-boot.its"
UBOOT_ITS_SYMLINK ?= "u-boot-its-${MACHINE}"
UBOOT_FITIMAGE_IMAGE ?= "u-boot-fitImage-${MACHINE}-${PV}-${PR}"
UBOOT_FITIMAGE_BINARY ?= "u-boot-fitImage"
UBOOT_FITIMAGE_SYMLINK ?= "u-boot-fitImage-${MACHINE}"
SPL_DIR ?= "spl"
SPL_DTB_IMAGE ?= "u-boot-spl-${MACHINE}-${PV}-${PR}.dtb"
SPL_DTB_BINARY ?= "u-boot-spl.dtb"
SPL_DTB_SYMLINK ?= "u-boot-spl-${MACHINE}.dtb"
SPL_NODTB_IMAGE ?= "u-boot-spl-nodtb-${MACHINE}-${PV}-${PR}.bin"
SPL_NODTB_BINARY ?= "u-boot-spl-nodtb.bin"
SPL_NODTB_SYMLINK ?= "u-boot-spl-nodtb-${MACHINE}.bin"

# U-Boot fitImage description
UBOOT_FIT_DESC ?= "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}"

# Kernel / U-Boot fitImage Hash Algo
FIT_HASH_ALG ?= "sha256"
UBOOT_FIT_HASH_ALG ?= "sha256"

# Kernel / U-Boot fitImage Signature Algo
FIT_SIGN_ALG ?= "rsa2048"
UBOOT_FIT_SIGN_ALG ?= "rsa2048"

# Kernel / U-Boot fitImage Padding Algo
# For Rockchip, it's RSA-PSS
FIT_PAD_ALG ?= "pss"

# Generate keys for signing Kernel / U-Boot fitImage
FIT_GENERATE_KEYS ?= "0"
UBOOT_FIT_GENERATE_KEYS ?= "0"

# Size of private keys in number of bits
# For Rockchip secure boot, should use same key and key size for
# u-boot and kernel FITs
UBOOT_FIT_SIGN_NUMBITS ?= "2048"

# args to openssl req (Default is -batch for non interactive mode and
# -new for new certificate)
UBOOT_FIT_KEY_REQ_ARGS ?= "-batch -new"

# Standard format for public key certificate
UBOOT_FIT_KEY_SIGN_PKCS ?= "-x509"

# We need (Rockchip-specific) u-boot-tools-native if we're creating a
# U-Boot fitImage
DEPENDS:append = "${@' rk-u-boot-tools-native dtc-native' if d.getVar('UBOOT_FITIMAGE_ENABLE') == '1' else ''}"
