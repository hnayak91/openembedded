DEPENDS = "adns ncurses virtual/libx11"
SECTION = "x11/games"
LICENSE = "LGPL"
PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/bzflag/bzflag-${PV}.tar.bz2"
S = "${WORKDIR}/bzflag-${PV}"

inherit autotools

EXTRA_OECONF = "--enable-bzadmin \
		--enable-client"

SRC_URI[md5sum] = "8e3e5fbef3cfa21079eb06269e6b3d8b"
SRC_URI[sha256sum] = "0329e3d0a59e9cc167733ed2b89a0dc2249725642a065cfd385bf1206fe30b19"
