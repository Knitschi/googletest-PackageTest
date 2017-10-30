#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/


// This function defines the build configurations.
def getBuildConfigurations()
{
    def configs = []
    //configs.addAll( getConfigsForVersion( '1.7.0-hunter-11' ) )    // latest package from old repository
    //configs.addAll() getConfigsForVersion( '1.8.0-hunter-p8' ) )    // latest hunter package
    configs.addAll( getConfigsForVersion( 'GIT_SUBMODULE' ) )      // developer version

    // Add indexes to to the node names for the master.
    def masterTagIndex = 0
    for(config in configs)
    {
        config['MasterTag'] = config['MasterTag'] + masterTagIndex
        masterTagIndex = masterTagIndex + 1
    }
    
    return configs
}

def getConfigsForVersion( version )
{
    def configs = []

    // Test common case of static build release build vor VS2015
    configs.add( getParameterMap(
        "Googletest-${version}-vs2015-static-release",
        'Windows-10',
        "-G\"Visual Studio 14 2015 Win64\" -DHUNTER_PACKAGE_VERSION=${version}", 
        '--config Release'
    ))
    
    /* 
    - Test the dynamic buiild.
    - Test a build other then debug that creates debug info.
    - Test that the package respects the CMAKE_<CONFIG>_POSTFIX variable. 
    */
    configs.add( getParameterMap(
        "Googletest-${version}-vs2015-dynamic-relwithdebinfo",
        'Windows-10',
        "-G\"Visual Studio 14 2015\" -DHUNTER_BUILD_SHARED_LIBS=ON -DHUNTER_PACKAGE_VERSION=${version} -DCMAKE_RELWITHDEBINFO_POSTFIX=-relwithdebinfo", 
        '--config RelWithDebInfo'
    ))

    /*
    - Test static lib build with gcc.
    - Test debug lib build with gcc.
    */
    configs.add( getParameterMap(
        "Googletest-${version}-make-static-debug",
        'Debian-8.9',
        "-G\"Unix Makefiles\" -DCMAKE_BUILD_TYPE=Debug -DHUNTER_PACKAGE_VERSION=${version}", 
        ''
    ))

    /*
    - Test dynamic lib build with gcc.
    */
    configs.add( getParameterMap(
        "Googletest-${version}-make-dynamic-release",
        'Debian-8.9',
        "-G\"Unix Makefiles\" -DCMAKE_BUILD_TYPE=Release -DHUNTER_BUILD_SHARED_LIBS=ON -DHUNTER_PACKAGE_VERSION=${version}", 
        ''
    ))

    return configs
}

def getParameterMap( checkoutDirectroy, buildSlaveTag, additionalGenerateArguments, additionalBuildArguments )
{
    def paramMap = [:]
    
    paramMap['RepositoryUrl'] = 'https://github.com/Knitschi/googletest-PackageTest.git'
    paramMap['CheckoutDirectory'] = checkoutDirectroy
    paramMap['MasterTag'] = 'master-'
    paramMap['BuildSlaveTag'] = buildSlaveTag
    paramMap['AdditionalGenerateArguments'] = additionalGenerateArguments
    paramMap['AdditionalBuildArguments'] = additionalBuildArguments
    
    return paramMap
}

// Trigger the jobs
stage('Run Builds')
{
    
    def configurations = getBuildConfigurations()
    
    // Add nodes for building the pipeline
    // For each configuration we create a "handle" node on the master, which itself
    // Starts a job on one of the build-slaves.
    def handleNodes = [:]
    for(config in configurations)
    {
        handleNodes[config['MasterTag']] = createMasterHandleNode(config)
    }
    // run the nodes
    parallel handleNodes
}

def createMasterHandleNode(config)
{
    return {
        node(config['MasterTag'])
        {
            def params = """
RepositoryUrl: ${config['RepositoryUrl']}
CheckoutDirectory: ${config['CheckoutDirectory']}
BuildSlaveTag: ${config['BuildSlaveTag']}
AdditionalGenerateArguments: ${config['AdditionalGenerateArguments']}
AdditionalBuildArguments: ${config['AdditionalBuildArguments']}
"""
            
            echo params
            
            build job: 'CMakeProjectBuildJob' , parameters: [
                    string(name: 'RepositoryUrl', value: config['RepositoryUrl'] ), 
                    string(name: 'CheckoutDirectory', value: config['CheckoutDirectory'] ), 
                    string(name: 'BuildSlaveTag', value: config['BuildSlaveTag'] ), 
                    string(name: 'AdditionalGenerateArguments', value: config['AdditionalGenerateArguments'] ), 
                    string(name: 'AdditionalBuildArguments', value: config['AdditionalBuildArguments'] )
                ] , quietPeriod: 0
        }
    }
}




