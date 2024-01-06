RK_OPTEE_SIGNING_KEY ??= ""
RK_OPTEE_SIGNING_CLASS ?= "${@'python3native' if d.getVar('RK_OPTEE_SIGNING_KEY') else ''}"

DEPENDS:append = "${@' rk-optee-signing-tools-native' if d.getVar('RK_OPTEE_SIGNING_KEY') else ''}"

do_sign_tas() {
    if [ -n "${RK_OPTEE_SIGNING_KEY}" ]; then
        find ${B} -type f -name '*.ta' | while read f; do
            resign_ta.py --key "${RK_OPTEE_SIGNING_KEY}" --in $f
        done
    fi
}
do_sign_tas[dirs] = "${B}"

# Placed here to allow a user-provided class to override the
# do_sign_tas function more easily
inherit ${RK_OPTEE_SIGNING_CLASS}

addtask sign_tas after do_compile before do_install

PACKAGE_ARCH = "${MACHINE_ARCH}"
