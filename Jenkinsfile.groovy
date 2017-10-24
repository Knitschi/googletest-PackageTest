#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/

// returns a map that is used to generate indexed build slave tags. 
/*
def getBuildSlaveTagIndexMap()
{
    def availableBaseTags = ['Windows-10','Debian-8.9']
    def slaveTagIndexMap = [:]
    
    for(baseTag in availableBaseTags)
    {
        slaveTagIndexMap[baseTag] = 0
    }

    return slaveTagIndexMap
}
*/

// This function defines the build configurations.
def getBuildConfigurations()
{
    def vs2015StaticDebug = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-vs2015-static-debug',
        'Windows-10',
        '-G"Visual Studio 14 2015"', 
        '--config Debug'
    )
    
    def vs2015StaticRelease = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-vs2015-static-release',
        'Windows-10',
        '-G"Visual Studio 14 2015"', 
        '--config Release'
    )
    
    def makeStaticRelease = getParameterMap(
        'https://github.com/Knitschi/googletest-PackageTest.git', 
        'Googletest-make-static-debug',
        'Windows-10',
        '-G"Unix Makefiles"', 
        '--config Debug'
    )

    return [vs2015StaticDebug,/*vs2015StaticRelease,*/makeStaticRelease]
}

def getParameterMap( repositoryUrl, checkoutDirectroy, buildSlaveTag, additionalGenerateArguments, additionalBuildArguments )
{
    def paramMap = [:]
    
    paramMap['RepositoryUrl'] = repositoryUrl
    paramMap['CheckoutDirectory'] = checkoutDirectroy
    paramMap['BuildSlaveTag'] = buildSlaveTag
    paramMap['AdditionalGenerateArguments'] = additionalGenerateArguments
    paramMap['AdditionalBuildArguments'] = additionalBuildArguments
    
    return paramMap
}

// Trigger the jobs
stage('Run Builds')
{
    // prepare a map for build slave tag index incrementation
    //def slaveTagIndexes = getBuildSlaveTagIndexMap()
    def configurations = getBuildConfigurations()
    
    // trigger the cmake project job for all configurations
    for(config in configurations)
    {
    
        def params = """
RepositoryUrl: ${params['RepositoryUrl']}
CheckoutDirectory: ${params['CheckoutDirectory']}
BuildSlaveTag: ${params['BuildSlaveTag']}
AdditionalGenerateArguments: ${params['AdditionalGenerateArguments']}
AdditionalBuildArguments: ${params['AdditionalBuildArguments']}
"""
        echo params
    
        build job: 'CMakeProjectBuildJob' , parameters: [
                string(name: 'RepositoryUrl', value: params['RepositoryUrl'] ), 
                string(name: 'CheckoutDirectory', value: params['CheckoutDirectory'] ), 
                string(name: 'BuildSlaveTag', value: params['BuildSlaveTag'] ), 
                string(name: 'AdditionalGenerateArguments', value: params['AdditionalGenerateArguments'] ), 
                string(name: 'AdditionalBuildArguments', value: params['AdditionalBuildArguments'] )
            ] , quietPeriod: 0
    }
}



