def image_boot_files(d):
    if d.getVar('KERNEL_IMAGETYPE') == "fitImage":
        if d.getVar('INITRAMFS_IMAGE') and (d.getVar('INITRAMFS_IMAGE_BUNDLE') or "") != "1":
            if d.getVar('INITRAMFS_IMAGE_NAME'):
                return "fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_LINK_NAME};fitImage"
            else:
                return "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${KERNEL_FIT_LINK_NAME};fitImage"
        return "fitImage"
    dtbs = [os.path.basename(dtb) for dtb in d.getVar('KERNEL_DEVICETREE').split()]
    return "${KERNEL_IMAGETYPE} " + ' '.join([dtb + ";rockchip/" + dtb for dtb in dtbs])
