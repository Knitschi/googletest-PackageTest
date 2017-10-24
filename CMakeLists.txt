
cmake_minimum_required (VERSION 3.4.0) 

include("${CMAKE_CURRENT_LIST_DIR}/cmake/Hunter/HunterGate.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/cmake/utilities.cmake")

setHunterRoot()
createHunterArchiveFromRepository( hunterArchiveFile hunterSHA1 )

HunterGate(
    URL ${hunterArchiveFile}
    SHA1 ${hunterSHA1}
    LOCAL
)

project(googletest-PackageTest)

# download and build GTest
hunter_add_package(GTest)

# Add executable that provides its own main implementation
find_package(GTest 1.8.0 CONFIG REQUIRED )

add_executable( GTestConsumer useGTest.cpp main.cpp )
target_link_libraries( GTestConsumer PUBLIC GTest::gtest )

# Add executable that uses the main() implementation from GMock:gmock_main
find_package(GMock 1.8.0 CONFIG )
if(TARGET GMock::main)
    add_executable( GMockConsumer useGMock.cpp )
    target_link_libraries( GMockConsumer PUBLIC GMock::main )
endif()
