
cmake_minimum_required (VERSION 3.4.0) 

include("cmake/Hunter/HunterGate.cmake")

function( setIfNotDefined variableName variableValue )
    if(NOT DEFINED ${variableName})
        set(${variableName} ${variableValue} PARENT_SCOPE)
    endif()
endfunction()

# set variable defaults to allow simpler command line call
setIfNotDefined( GTEST_VERSION 1.8.0-hunter-p5)
setIfNotDefined( HUNTER_URL https://github.com/ruslo/hunter/archive/v0.19.131.tar.gz )
setIfNotDefined( HUNTER_SHA1 bc0eec34712af8854085c2dc6a90b78df17a9743 )

HunterGate(
    URL ${HUNTER_URL}
    SHA1 ${HUNTER_SHA1}
    LOCAL
)


project(googletest-PackageTest)

hunter_add_package(GTest)


# Add executable that uses the main() implementation from GMock:gmock_main
add_executable( GTestConsumer1 useGMock.cpp )
target_link_libraries( GTestConsumer1 PUBLIC GMock::gmock_main )

# Add executable that provides its own main implementation
add_executable( GTestConsumer2 useGTest.cpp main.cpp )
target_link_libraries( GTestConsumer1 PUBLIC GTest::gtest )
