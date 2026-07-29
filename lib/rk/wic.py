def image_boot_files(d):
    if d.getVar('INITRAMFS_IMAGE') and (d.getVar('INITRAMFS_IMAGE_BUNDLE') or "") != "1":
        if d.getVar('INITRAMFS_IMAGE_NAME'):
            return "fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_LINK_NAME};fitImage"
        else:
            return "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${KERNEL_FIT_LINK_NAME};fitImage"
    return "fitImage"
