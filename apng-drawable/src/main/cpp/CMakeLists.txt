cmake_minimum_required(VERSION 3.4.1)

#
# png
#

set(png_source_dir
        ${CMAKE_CURRENT_SOURCE_DIR}/libpng
        )

set(png_headers
        ${png_source_dir}/pnginfo.h
        ${png_source_dir}/config.h
        ${png_source_dir}/png.h
        ${png_source_dir}/pngconf.h
        ${png_source_dir}/pngdebug.h
        ${png_source_dir}/pnglibconf.h
        ${png_source_dir}/pngpriv.h
        ${png_source_dir}/pngstruct.h
        )

set(png_headers
        ${png_source_dir}/arm/arm_init.c
        ${png_source_dir}/arm/filter_neon.S
        ${png_source_dir}/arm/filter_neon_intrinsics.c
        ${png_source_dir}/png.c
        ${png_source_dir}/pngerror.c
        ${png_source_dir}/pngget.c
        ${png_source_dir}/pngmem.c
        ${png_source_dir}/pngpread.c
        ${png_source_dir}/pngread.c
        ${png_source_dir}/pngrio.c
        ${png_source_dir}/pngrtran.c
        ${png_source_dir}/pngrutil.c
        ${png_source_dir}/pngset.c
        ${png_source_dir}/pngtrans.c
        ${png_source_dir}/pngwio.c
        ${png_source_dir}/pngwrite.c
        ${png_source_dir}/pngwtran.c
        ${png_source_dir}/pngwutil.c
        )

add_library(png
        STATIC
        ${png_sources}
        ${png_headers}
        )

target_link_libraries(png
        -lz
        )

#
# apng-drawable
#

set(apng_drawbale_source_dir
        ${CMAKE_CURRENT_SOURCE_DIR}/apng-drawbale
        )

set(apng_drawbale_headers
        ${apng_drawbale_source_dir}/ApngDecoder.h
        ${apng_drawbale_source_dir}/ApngFrame.h
        ${apng_drawbale_source_dir}/ApngImage.h
        ${apng_drawbale_source_dir}/Error.h
        ${apng_drawbale_source_dir}/Log.h
        ${apng_drawbale_source_dir}/StreamSource.h
        )

set(apng_drawbale_sources
        ${apng_drawbale_source_dir}/ApngDecoder.cpp
        ${apng_drawbale_source_dir}/ApngDecoderJni.cpp
        ${apng_drawbale_source_dir}/ApngFrame.cpp
        ${apng_drawbale_source_dir}/ApngImage.cpp
        ${apng_drawbale_source_dir}/StreamSource.cpp
        )

add_library(apng-drawable
        SHARED
        ${apng_drawbale_headers}
        ${apng_drawbale_sources}
        )

target_include_directories(
        apng-drawable
        PUBLIC
        ${png_source_dir}
)

if (${CMAKE_BUILD_TYPE} MATCHES DEBUG)
    find_library(log-lib
            log
            )

    target_link_libraries(apng-drawable
            ${log-lib}
            -ljnigraphics
            png
            )
else (${CMAKE_BUILD_TYPE} MATCHES DEBUG)
    target_link_libraries(apng-drawable
            -ljnigraphics
            png
            )
endif (${CMAKE_BUILD_TYPE} MATCHES DEBUG)
