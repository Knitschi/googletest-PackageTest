#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/


// This function defines the build configurations.
def getBuildConfigurations()
{
    def vs2015StaticDebug = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-vs2015-static-debug',
        'master-01',
        'Windows-10',
        '-G"Visual Studio 14 2015"', 
        '--config Debug'
    )
    
    def vs2015StaticRelease = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-vs2015-static-release',
        'master-02',
        'Windows-10',
        '-G"Visual Studio 14 2015"', 
        '--config Release'
    )
    
    def makeStaticRelease = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-make-static-debug',
        'master-03',
        'Debian-8.9',
        '-G"Unix Makefiles"', 
        '--config Debug'
    )

    return [vs2015StaticDebug,vs2015StaticRelease,makeStaticRelease]
}

def getParameterMap( repositoryUrl, checkoutDirectroy, masterTag, buildSlaveTag, additionalGenerateArguments, additionalBuildArguments )
{
    def paramMap = [:]
    
    paramMap['RepositoryUrl'] = repositoryUrl
    paramMap['CheckoutDirectory'] = checkoutDirectroy
    paramMap['MasterTag'] = masterTag
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
        node('config['MasterTag']')
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




