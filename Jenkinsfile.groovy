#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/


// This function defines the build configurations.
def getBuildConfigurations()
{
    def configs = []

    configs.add( getParameterMap(
        'Googletest-vs2015-static-debug',
        'Windows-10',
        '-G"Visual Studio 14 2015"', 
        '--config Debug'
    ))
    
    configs.add( getParameterMap(
        'Googletest-vs2015-static-release',
        'Windows-10',
        '-G"Visual Studio 14 2015 Win64"', 
        '--config Release'
    ))
    
    configs.add( getParameterMap(
        'Googletest-vs2015-dynamic-release',
        'Windows-10',
        '-G"Visual Studio 14 2015" -DHUNTER_BUILD_SHARED_LIBS=ON', 
        '--config Release'
    ))
    
    configs.add( getParameterMap(
        'Googletest-vs2015-dynamic-debug',
        'Windows-10',
        '-G"Visual Studio 14 2015 Win64" -DHUNTER_BUILD_SHARED_LIBS=ON', 
        '--config Debug'
    ))
    
    configs.add( getParameterMap(
        'Googletest-make-static-debug',
        'Debian-8.9',
        '-G"Unix Makefiles"', 
        '--config Debug'
    ))

    // Add indexes to to the node names for the master.
    def masterTagIndex = 0
    for(config in configs)
    {
        config['MasterTag'] = config['MasterTag'] + masterTagIndex
        masterTagIndex = masterTagIndex + 1
    }
    
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




