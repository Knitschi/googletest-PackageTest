include(hunter_config)
include(hunter_user_error)
hunter_config( GTest GIT_SUBMODULE googletest CMAKE_ARGS HUNTER_INSTALL_LICENSE_FILES=googletest/LICENSE )
        