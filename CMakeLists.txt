
cmake_minimum_required (VERSION 3.4.0) 

include("${CMAKE_CURRENT_LIST_DIR}/cmake/Hunter/HunterGate.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/utilities.cmake")

if(NOT DEFINED HUNTER_PACKAGE_VERSION)
    set( HUNTER_PACKAGE_VERSION GIT_SUBMODULE)
endif()

setHunterRoot()
createHunterArchiveFromRepository( hunterArchiveFile hunterSHA1 )
createHunterConfigFile( ${HUNTER_PACKAGE_VERSION} ) 

set( HUNTER_STATUS_DEBUG ON )

HunterGate(
    URL ${hunterArchiveFile}
    SHA1 ${hunterSHA1}
    LOCAL
)

project(googletest-PackageTest)

# download and build GTest
hunter_add_package(GTest)

# Add executable that provides its own main implementation
find_package(GTest CONFIG REQUIRED )

add_executable( GTestConsumer useGTest.cpp main.cpp )
target_link_libraries( GTestConsumer PUBLIC GTest::gtest )

add_executable( GTestMainConsumer useGTest.cpp )
target_link_libraries( GTestMainConsumer PUBLIC GTest::main )

# Add executable that uses the main() implementation from GMock:gmock_main
# Package versions < 1.8.0-hunter-p2 do not include GMock
find_package(GMock CONFIG )
if(GMock_FOUND)
    add_executable( GMockConsumer useGMock.cpp )
    target_link_libraries( GMockConsumer PUBLIC GMock::main  )
endif()

set(targets GTest::gtest GTest::main GMock::gmock GMock::main)
printInterfaceDefinitions( "${targets}" )
