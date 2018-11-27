#!/bin/bash -eu

cd `dirname $0`

print_help_and_exit () {
    cat <<EOF
usage: ./`basename $0` <libpng_version>
Downloads libpng source code and applies apng supporting patch.
If you want to use the project specified version, run "cat libpng_version | xargs ./`basename $0`".
EOF
    exit
}

if [ $# -ne 1 ]; then
  print_help_and_exit
fi

case "$1" in
  -h|-?|--help)
    print_help_and_exit
    exit
    ;;
esac

LIBPNG_VERSION="$1"

echo libpng version = ${LIBPNG_VERSION}
LIBPNG_VERSION_MAJOR=${LIBPNG_VERSION%%.*}
LIBPNG_VERSION_MINOR=$(MAJOR_MINOR=${LIBPNG_VERSION%.*};echo ${MAJOR_MINOR#*.})

# Creates tmp dir
rm -rf ./tmp
mkdir ./tmp
cd ./tmp

echo "Downloading libpng-$LIBPNG_VERSION.tar.gz"
curl -L https://download.sourceforge.net/libpng/libpng-${LIBPNG_VERSION}.tar.gz > ./libpng-${LIBPNG_VERSION}.tar.gz

echo "Downloading libpng-$LIBPNG_VERSION-apng.patch.gz"
curl -L https://download.sourceforge.net/libpng-apng/libpng${LIBPNG_VERSION_MAJOR}${LIBPNG_VERSION_MINOR}/${LIBPNG_VERSION}/libpng-${LIBPNG_VERSION}-apng.patch.gz > ./libpng-${LIBPNG_VERSION}-apng.patch.gz

echo "Unarchiving libpng-$LIBPNG_VERSION.tar.gz"
tar -zxvf ./libpng-${LIBPNG_VERSION}.tar.gz -C ./
mv ./libpng-${LIBPNG_VERSION} ./libpng-${LIBPNG_VERSION}.org

echo "Unarchiving libpng-$LIBPNG_VERSION-apng.patch.gz"
gzip -d ./libpng-${LIBPNG_VERSION}-apng.patch.gz

echo "Reflecting the apng patches"
patch -p0 < ./libpng-${LIBPNG_VERSION}-apng.patch
cp ./libpng-${LIBPNG_VERSION}.org/scripts/pnglibconf.h.prebuilt ./libpng-${LIBPNG_VERSION}.org/pnglibconf.h

echo "Moving the patched files"
rm -rf ../apng-drawable/src/main/cpp/libpng/
mv ./libpng-${LIBPNG_VERSION}.org ../apng-drawable/src/main/cpp/libpng/

echo "Finished!"
