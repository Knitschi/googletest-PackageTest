# This file defines the version of the GTest package that is tested.

include(hunter_config)
include(hunter_user_error)

hunter_config(GTest GIT_SUBMODULE googletest)
#hunter_config(GTest VERSION 1.8.0-hunter-p7)