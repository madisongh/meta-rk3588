import logging
import os

from wic import WicError
from wic.misc import BB_VARS, exec_native_cmd
from wic.plugins.imager.direct import DirectPlugin
from oe import path

logger = logging.getLogger('wic')

class RkUpdateImagePlugin(DirectPlugin):
    name = "rkupdateimg"

    def __init__(self, wks_file, rootfs_dir, bootimg_dir, kernel_dir,
                 native_sysroot, oe_builddir, options):
        super().__init__(wks_file, rootfs_dir, bootimg_dir, kernel_dir,
                         native_sysroot, oe_builddir, options)
        if self.ptable_format != "gpt":
            raise WicError("{} format requires GPT partition table".format(self.name))
        self.output_file_path = self._full_path(self.workdir, self.parts[0].disk, "rkupdateimg")
        self.to_remove = []

    def assemble(self):
        self.generate_parameter_file()
        self.generate_package_file()
        self.assemble_package()

    def finalize(self):
        # We throw away the regular disk image, which we only really
        # needed for figuring out partition offsets and sizes.
        path.remove(self._image.path, recurse=False)
        # And the partition contents - actual files - that were
        # added to the tmp work directory, so they don't get copied
        # to the output directory (symlinks are automatically skipped).
        for tmpfile in self.to_remove:
            path.remove(tmpfile, recurse=False)

    def print_info(self):
        logger.info("Rockchip update package generated at: " + self.output_file_path)

    def generate_parameter_file(self):
        image_basename = os.path.splitext(os.path.basename(self._image.path))[0]
        with open(os.path.join(self.workdir, "parameter.txt"), "w") as pf:
            print("# IMAGE_NAME: " + image_basename, file=pf)
            print("FIRMWARE_VER: 1.0", file=pf)
            print("TYPE: GPT", file=pf)
            named_partitions = [p for p in self.parts if p.part_name]
            if (BB_VARS.get_var("RK_PARTITION_GROW") or "") == "1":
                partinfo = ",".join(["0x%08x@0x%08x(%s)" % (p.size_sec,
                                                            p.start,
                                                            p.part_name) for p in named_partitions[:-1]])
                partinfo += ",-@0x%08x(%s)" % (named_partitions[-1].offset, named_partitions[-1].part_name)
            else:
                partinfo = ",".join(["0x%08x@0x%08x(%s)" % (p.size_sec,
                                                            p.start,
                                                            p.part_name) for p in named_partitions])
            print("CMDLINE:mtdparts=rk29xxnand:" + partinfo, file=pf)
            for p in self.parts:
                if p.mountpoint == "/" and p.use_uuid and p.uuid:
                    print("uuid: {}={}".format(p.part_name, p.uuid), file=pf)
                    break

    def generate_package_file(self):
        image_basename = os.path.splitext(os.path.basename(self._image.path))[0]
        loader_name = BB_VARS.get_var('RK_LOADER_BIN') or "loader.bin"
        path.symlink(os.path.join(BB_VARS.get_var('DEPLOY_DIR_IMAGE'), loader_name),
                     os.path.join(self.workdir, loader_name))
        with open(os.path.join(self.workdir, "package-file"), "w") as pf:
            print("# IMAGE_NAME: " + image_basename, file=pf)
            print("bootloader " + loader_name, file=pf)
            print("parameter parameter.txt", file=pf)
            for p in self.parts:
                if p.part_name and p.source_file:
                    # The tools expect files to be together in one directory,
                    # so symlink in any files that aren't already here.
                    source_file_name = os.path.basename(p.source_file)
                    source_file_dir = os.path.dirname(p.source_file)
                    if os.path.realpath(source_file_dir) == os.path.realpath(self.workdir):
                        self.to_remove.append(p.source_file)
                    else:
                        path.symlink(p.source_file, os.path.join(self.workdir, source_file_name))
                    print(p.part_name + " " + source_file_name, file=pf)

    def assemble_package(self):
        oldwd = os.getcwd()
        os.chdir(self.workdir)
        out_file = os.path.basename(self.output_file_path)
        loader_name = BB_VARS.get_var('RK_LOADER_BIN') or "loader.bin"
        chip = (BB_VARS.get_var('RK_UPDATE_CHIP') or BB_VARS.get_var('SOC_FAMILY')).upper()
        try:
            exec_native_cmd("afptool -pack ./ update.raw.img", self.native_sysroot)
            exec_native_cmd("rkImageMaker -{} {} update.raw.img {} -os_type:androidos".format(chip,
                                                                                              loader_name,
                                                                                              out_file),
                            self.native_sysroot)
        finally:
            os.chdir(oldwd)
