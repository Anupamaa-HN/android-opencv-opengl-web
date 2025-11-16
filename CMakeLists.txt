cmake_minimum_required(VERSION 3.10)
project(edgeproc)

set(CMAKE_CXX_STANDARD 17)

# Adjust OpenCV path when building on machine / CI
if(NOT DEFINED OpenCV_DIR)
    set(OpenCV_DIR "${CMAKE_SOURCE_DIR}/opencv-android/sdk/native/jni")
endif()

find_package(OpenCV REQUIRED COMPONENTS core imgproc imgcodecs highgui)

add_library(edgeproc SHARED jni/native.cpp)

include_directories(${OpenCV_INCLUDE_DIRS})
target_link_libraries(edgeproc ${OpenCV_LIBS} android log)
