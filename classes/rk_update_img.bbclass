inherit image_types_wic

# Variables needed for the rkupdateimg wic plugin
#
# This is passed to rkImageMaker to identify the target SoC
RK_UPDATE_CHIP ?= "${SOC_FAMILY}"

# This is the SPL loader binary
RK_LOADER_BIN ?= "loader.bin"

WICVARS:append = " RK_UPDATE_CHIP RK_LOADER_BIN"

RK_UPDATE_WIC_EXTRA_ARGS ?= ""

# This is implemented as a separate image type to allow for
# continued use of normal wic images with rkdeveloptool, alongside
# update.img packges for use with Rockchip's upgrade_tool and
# Windows-based tools.
IMAGE_CMD:rkupdateimg () {
	out="${IMGDEPLOYDIR}/${IMAGE_NAME}"
	build_wic="${WORKDIR}/build-rkupdate"
	tmp_wic="${WORKDIR}/tmp-rkupdate"
	wks="${WKS_FULL_PATH}"
	if [ -e "$tmp_wic" ]; then
		# Ensure we don't have any junk leftover from a previously interrupted
		# do_image_wic execution
		rm -rf "$tmp_wic"
	fi
	if [ -z "$wks" ]; then
		bbfatal "No kickstart files from WKS_FILES were found: ${WKS_FILES}. Please set WKS_FILE or WKS_FILES appropriately."
	fi
	BUILDDIR="${TOPDIR}" PSEUDO_UNLOAD=1 wic create "$wks" --vars "${STAGING_DIR}/${MACHINE}/imgdata/" -e "${IMAGE_BASENAME}" -o "$build_wic/" -w "$tmp_wic" -i rkupdateimg ${WIC_CREATE_EXTRA_ARGS}
	mv "$build_wic/$(basename "${wks%.wks}")"*.rkupdateimg "$out${IMAGE_NAME_SUFFIX}.rkupdateimg"
	ln -sf ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.rkupdateimg "${IMGDEPLOYDIR}/update.img"
}
IMAGE_CMD:rkupdateimg[vardepsexclude] = "WKS_FULL_PATH WKS_FILES TOPDIR"
do_image_rkupdateimg[cleandirs] = "${WORKDIR}/build-rkupdate"

PSEUDO_IGNORE_PATHS .= ",${WORKDIR}/build-rkupdate"

# Rebuild when the wks file or vars in WICVARS change
USING_RK_UPDATE = "${@bb.utils.contains('IMAGE_FSTYPES', 'rkupdateimg', '1', '', d)}"
RK_WKS_FILE_CHECKSUM = "${@'${WKS_FULL_PATH}:%s' % os.path.exists('${WKS_FULL_PATH}') if '${USING_RK_UPDATE}' else ''}"
do_image_rkupdateimg[file-checksums] += "${RK_WKS_FILE_CHECKSUM}"
do_image_rkupdateimg[depends] += "rk-linux-pack-firmware-native:do_populate_sysroot"

# We ensure all artfacts are deployed (e.g virtual/bootloader)
do_image_rkupdateimg[recrdeptask] += "do_deploy"
do_image_rkupdateimg[deptask] += "do_image_complete"

RK_WKS_FILE_DEPENDS_DEFAULT = "e2fsprogs-native"
RK_WKS_FILE_DEPENDS ??= "${WKS_FILE_DEPENDS_DEFAULT}"

DEPENDS += "${@ '${RK_WKS_FILE_DEPENDS}' if d.getVar('USING_RK_UPDATE') else '' }"

python () {
    if d.getVar('USING_RK_UPDATE'):
        wks_file_u = d.getVar('WKS_FULL_PATH', False)
        wks_file = d.expand(wks_file_u)
        base, ext = os.path.splitext(wks_file)
        if ext == '.in' and os.path.exists(wks_file):
            wks_out_file = os.path.join(d.getVar('WORKDIR'), os.path.basename(base))
            d.setVar('WKS_FULL_PATH', wks_out_file)
            d.setVar('WKS_TEMPLATE_PATH', wks_file_u)
            d.setVar('RK_WKS_FILE_CHECKSUM', '${WKS_TEMPLATE_PATH}:True')

            # We need to re-parse each time the file changes, and bitbake
            # needs to be told about that explicitly.
            bb.parse.mark_dependency(d, wks_file)

            try:
                with open(wks_file, 'r') as f:
                    body = f.read()
            except (IOError, OSError) as exc:
                pass
            else:
                # Previously, I used expandWithRefs to get the dependency list
                # and add it to WICVARS, but there's no point re-parsing the
                # file in process_wks_template as well, so just put it in
                # a variable and let the metadata deal with the deps.
                d.setVar('_WKS_TEMPLATE', body)
                bb.build.addtask('do_write_wks_template', 'do_image_rkupdateimg', 'do_image', d)
        bb.build.addtask('do_image_rkupdateimg', 'do_image_complete', None, d)
}

addtask do_rootfs_wicenv after do_image before do_image_rkupdateimg
