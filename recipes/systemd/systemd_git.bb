DESCRIPTION = "Systemd a init replacement"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "readline udev dbus libcap2 libcgroup"
DEPENDS += "${@base_contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

PRIORITY = "optional"
SECTION = "base/shell"

inherit gitpkgv
PKGV = "v${GITPKGVTAG}"

# This gets reset to the proper version with PKGV above
PV = "git"
PR = "r10"

inherit autotools vala update-alternatives

SRCREV = "da2617378523e007ec0c6efe99d0cebb2be994e1"

SRC_URI = "git://anongit.freedesktop.org/systemd;protocol=git \
           file://execute.patch \
           file://0001-systemd-disable-xml-file-stuff-and-introspection.patch \
           file://serial-getty@.service \
          "

S = "${WORKDIR}/git"

SYSTEMDDISTRO ?= "debian"
SYSTEMDDISTRO_angstrom = "angstrom"

# The gtk+ tools should get built as a separate recipe e.g. systemd-tools
EXTRA_OECONF = " --with-distro=${SYSTEMDDISTRO} \
                 --with-rootdir=${base_prefix} \
                 ${@base_contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)} \
                 --disable-gtk \
               "

def get_baudrate(bb, d):
    return bb.data.getVar('SERIAL_CONSOLE', d, 1).split()[0]

def get_console(bb, d):
    return bb.data.getVar('SERIAL_CONSOLE', d, 1).split()[1]

do_install_append() {
        if [ ! ${@get_baudrate(bb, d)} = "" ]; then
          sed -i -e s/\@BAUDRATE\@/${@get_baudrate(bb, d)}/g ${WORKDIR}/serial-getty@.service
          install ${WORKDIR}/serial-getty@.service ${D}${base_libdir}/systemd/system/
          ln -sf ${base_libdir}/systemd/system/serial-getty@.service \
              ${D}${sysconfdir}/systemd/system/getty.target.wants/getty@${@get_console(bb, d)}.service
        fi
}

ALTERNATIVE_NAME = "init"
ALTERNATIVE_LINK = "${base_sbindir}/init"
ALTERNATIVE_PATH = "${base_bindir}/systemd"
ALTERNATIVE_PRIORITY = "80"

PACKAGES =+ "${PN}-gui ${PN}-serialgetty"

FILES_${PN}-gui = "${bindir}/systemadm"

# This is a machine specific file
FILES_${PN}-serialgetty = "${base_libdir}/systemd/system/serial-getty@.service ${sysconfdir}/systemd/system/getty.target.wants/getty@${@get_console(bb, d)}.service"
PACKAGE_ARCH_${PN}-serialgetty = "${MACHINE_ARCH}"

FILES_${PN} = " ${base_bindir}/* \
                ${datadir}/dbus-1/services \
                ${datadir}/dbus-1/system-services \
                ${datadir}/polkit-1 \
                ${datadir}/${PN} \
                ${sysconfdir} \
                ${base_libdir}/systemd/* \
                ${base_libdir}/systemd/system/* \
                ${base_libdir}/udev/rules.d \
                ${base_libdir}/security/*.so \
                /cgroup \
                ${bindir}/systemd* \
                ${libdir}/tmpfiles.d/*.conf \
                ${libdir}/systemd \
               "

FILES_${PN}-dbg += "${base_libdir}/systemd/.debug ${base_libdir}/systemd/*/.debug"

# kbd -> loadkeys,setfont
RRECOMMENDS_${PN} += "kbd kbd-consolefonts ${PN}-serialgetty"

