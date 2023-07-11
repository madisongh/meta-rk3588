DESCRIPTION = "WiFi/BT firmware redistributed by Rockchip to accompany Rockchip drivers"
LICENSE = "Firmware-rk-bcm43xx & Firmware-rk-rtlwifi"
LIC_FILES_CHKSUM = "file://extra/LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
		    file://extra/LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5"


EXTRA_SRC_REPO = "github.com/madisongh/rkwifibt-firmware.git;protocol=https"
EXTRA_SRC_URI = ";name=rkwifibt;subdir=rkwifibt/main git://${EXTRA_SRC_REPO};branch=${EXTRA_SRCBRANCH};subdir=rkwifibt/extra;name=extra"
EXTRA_SRCBRANCH = "main"
SRCREV_rkwifibt = "${RKWIFIBT_SRCREV}"
SRCREV_extra = "fd28a3c733b0cbdad7fcfa688254ba1b01c2d347"
SRCREV_FORMAT = "rkwifibt_extra"

require rkwifibt.inc

S = "${WORKDIR}/rkwifibt"
B = "${S}"

PV = "1.0+git${SRCPV}"

inherit allarch

# XXX: This is just a subset for now
RKWIFIBT_FIRMWARE_PATHS ?= "\
   broadcom/AP6398SV \
   realtek/RTL8852BS \
"

do_compile() {
    :
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware
    for fp in ${RKWIFIBT_FIRMWARE_PATHS}; do
        install -m 0644 -t ${D}${nonarch_base_libdir}/firmware $(find ${S}/main/firmware/$fp -type f 2>/dev/null) $(find ${S}/extra/$fp -type f 2>/dev/null)
    done
    # FIXME: rtk_hciattach tool looks in /lib/firmware/rtlbt
    for rtlf in ${D}${nonarch_base_libdir}/firmware/*rtl*_*; do
	[ -f "$rtlf" ] || continue
	[ -d ${D}${nonarch_base_libdir}/firmware/rtlbt ] || install -d ${D}${nonarch_base_libdir}/firmware/rtlbt
	ln -sf ../$(basename "$rtlf") ${D}${nonarch_base_libdir}/firmware/rtlbt/$(basename "$rtlf")
    done
    install -m 0644 -t ${D}${nonarch_base_libdir}/firmware ${S}/extra/LICENCE.*
}

PACKAGES =+ "${PN}-ap6398sv ${PN}-broadcom-license ${PN}-rtl8852bs ${PN}-rtlwifi-license"
NO_GENERIC_LICENSE[Firmware-rk-bcm43xx] = "extra/LICENCE.broadcom_bcm43xx"
FILES:${PN}-broadcom-license = "${nonarch_base_libdir}/firmware/LICENCE.broadcom_bcm43xx"
LICENSE:${PN}-broadcom-license = "Firmware-rk-bcm43xx"
NO_GENERIC_LICENSE[Firmware-rk-rtlwifi] = "extra/LICENCE.rtlwifi_firmware.txt"
FILES:${PN}-rtlwifi-license = "${nonarch_base_libdir}/firmware/LICENCE.rtlwifi_firmware.txt"
LICENSE:${PN}-rtlwifi-license = "Firmware-rk-rtlwifi"

FILES:${PN}-ap6398sv = "\
    ${nonarch_base_libdir}/firmware/BCM4362A2.hcd \
    ${nonarch_base_libdir}/firmware/clm_bcm4359c51a2_ag.blob \
    ${nonarch_base_libdir}/firmware/fw_bcm4359c51a2_ag_apsta.bin \
    ${nonarch_base_libdir}/firmware/fw_bcm4359c51a2_ag.bin \
    ${nonarch_base_libdir}/firmware/nvram_ap6398sv.txt \
"
LICENSE:${PN}-ap6398sv = "Firmware-rk-bcm43xx"
RDEPENDS:${PN}-ap6398sv = "${PN}-broadcom-license"

FILES:${PN}-rtl8852bs = "\
    ${nonarch_base_libdir}/firmware/rtl8852bs_config \
    ${nonarch_base_libdir}/firmware/rtl8852bs_fw \
    ${nonarch_base_libdir}/firmware/rtlbt/rtl8852bs_config \
    ${nonarch_base_libdir}/firmware/rtlbt/rtl8852bs_fw \
"
LICENSE:${PN}-rtl8852bs = "Firmware-rk-rtlwifi"
RDEPENDS:${PN}-rtl8852bs = "${PN}-rtlwifi-license"

ALLOW_EMPTY:${PN} = "1"
RRECOMMENDS:${PN} = "${@' '.join(oe.utils.packages_filter_out_system(d))}"
INSANE_SKIP = "arch"
SYSROOT_DIRS_IGNORE += "${nonarch_base_libdir}/firmware"
