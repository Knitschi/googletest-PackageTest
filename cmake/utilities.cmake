

# set the HUNTER_ROOT variable to a default value
function( setHunterRoot )
    # set hunter root to a directory for testing packages
    if(CMAKE_HOST_UNIX)
        set( HUNTER_ROOT "$ENV{HOME}/HunterPackageTests" PARENT_SCOPE)
    else()
        file(TO_CMAKE_PATH "$ENV{HOMEDRIVE}$ENV{HOMEPATH}\\HunterPackageTests" hunterRootLocal)
        set( HUNTER_ROOT ${hunterRootLocal} PARENT_SCOPE)
    endif()
endfunction()

# checkout the hunter sources from a repository and create an tar.gz archive with a hash that can be used for HunterGate()
function( createHunterArchiveFromRepository hunterArchiveFileOut archiveSHA1Out )

    # create archive of the hunter git submodule
    set( hunterArchiveFile "${CMAKE_BINARY_DIR}/hunter.tar.gz" )
    executeProcess( "git submodule update --recursive --remote" "${CMAKE_SOURCE_DIR}")
    executeProcess( "git archive -o \"${hunterArchiveFile}\" HEAD" "${CMAKE_SOURCE_DIR}/hunter" )
    # create hash
    file( SHA1 ${hunterArchiveFile} hunterSHA1)
    
    set( ${hunterArchiveFileOut} ${hunterArchiveFile} PARENT_SCOPE)
    set( ${archiveSHA1Out} ${hunterSHA1} PARENT_SCOPE)
    
endfunction()

function( executeProcess command workingDirectory )
    separate_arguments( commandList UNIX_COMMAND ${command} )
    execute_process(  
        COMMAND ${commandList}
        WORKING_DIRECTORY "${workingDirectory}"
    )
endfunction()

function( createHunterConfigFile hunterPackageVersion )

    set(filepath "${CMAKE_SOURCE_DIR}/cmake/Hunter/config.cmake")
    file(REMOVE "${filePath}")
    
    if( ${hunterPackageVersion} STREQUAL GIT_SUBMODULE )
        set( fileContent "\
include(hunter_config)\n\
include(hunter_user_error)\n\
hunter_config( GTest GIT_SUBMODULE googletest CMAKE_ARGS HUNTER_INSTALL_LICENSE_FILES=googletest/LICENSE )\n\
        ")
    else()
        set( fileContent "\
include(hunter_config)\n\
include(hunter_user_error)\n\
hunter_config(GTest VERSION ${PACKAGE_VERSION})\n\
        ")
    endif()
    file( WRITE "${filepath}" "${fileContent}")
    
endfunction()