DESCRIPTION = "A sophisticated Numeric Processing Package for Python"
SECTION = "devel/python"
PRIORITY = "optional"
LICENSE = "PSF"
PR = "ml0"

SRC_URI = "${SOURCEFORGE_MIRROR}/numpy/numpy-${PV}.tar.gz \
	   file://config.h \
	   file://numpyconfig.h \
	  "

S = "${WORKDIR}/numpy-${PV}"

inherit distutils

# Make the build fail and replace *config.h with proper one
# This is a ugly, ugly hack - Koen
do_compile_prepend() {
         BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
         ${STAGING_BINDIR_NATIVE}/python setup.py build ${DISTUTILS_BUILD_ARGS} || \
         true
	 cp ${WORKDIR}/*config.h ${S}/build/$(ls ${S}/build | grep src)/numpy/core/include/numpy/
}

SRC_URI[md5sum] = "2bce18c08fc4fce461656f0f4dd9103e"
SRC_URI[sha256sum] = "788b1bc712ee566d4b4d62ef99736c5830fa264cbc56f8651ded1e795c755cdd"
