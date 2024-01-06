RK_OPTEE_SIGNING_KEY ??= ""
RK_OPTEE_SIGNING_CLASS ?= "${@'python3native' if d.getVar('RK_OPTEE_SIGNING_KEY') else ''}"
inherit ${RK_OPTEE_SIGNING_CLASS}

DEPENDS:append = "${@' rk-optee-signing-tools-native' if d.getVar('RK_OPTEE_SIGNING_KEY') else ''}"

do_sign_tas() {
    if [ -n "${RK_OPTEE_SIGNING_KEY}" ]; then
        find ${B} -type f -name '*.ta' | while read f; do
            resign_ta.py --key "${RK_OPTEE_SIGNING_KEY}" --in $f
        done
    fi
}
do_sign_tas[dirs] = "${B}"

addtask sign_tas after do_compile before do_install

PACKAGE_ARCH = "${MACHINE_ARCH}"
