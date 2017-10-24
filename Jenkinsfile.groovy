#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/

// This class is a collection of build parameters for the CMakeProjectBuildJob
class CMakeProjectParameter {
    def repositoryUrl
    def checkoutDirectory
    def buildSlaveTag
    def additionalGenerateArguments
    def additionalBuildArguments
}

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
    def vs2015StaticDebug = new CMakeProjectParameter()
    vs2015StaticDebug.repositoryUrl = "https://github.com/Knitschi/googletest-PackageTest.git"
    vs2015StaticDebug.checkoutDirectory = "Googletest-vs2015-static-debug"
    vs2015StaticDebug.buildSlaveTag = "Windows-10"
    vs2015StaticDebug.additionalGenerateArguments = '-G"Visual Studio 14 2015"'
    vs2015StaticDebug.additionalBuildArguments = '--config Debug'
    
    def vs2015StaticRelease = new CMakeProjectParameter()
    vs2015StaticDebug.repositoryUrl = "https://github.com/Knitschi/googletest-PackageTest.git"
    vs2015StaticDebug.checkoutDirectory = "Googletest-vs2015-static-release"
    vs2015StaticDebug.buildSlaveTag = "Windows-10"
    vs2015StaticDebug.additionalGenerateArguments = '-G"Visual Studio 14 2015"'
    vs2015StaticDebug.additionalBuildArguments = '--config Release'
    
    def vs2015StaticRelease = new CMakeProjectParameter()
    vs2015StaticDebug.repositoryUrl = "https://github.com/Knitschi/googletest-PackageTest.git"
    vs2015StaticDebug.checkoutDirectory = "Googletest-make-static-release"
    vs2015StaticDebug.buildSlaveTag = "Windows-10"
    vs2015StaticDebug.additionalGenerateArguments = '-G"Unix Makefiles"'
    vs2015StaticDebug.additionalBuildArguments = '--config Release'
    
    return [vs2015StaticDebug,vs2015StaticRelease]
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
        build job: 'CMakeProjectBuildJob', parameters: [
            string(name: 'RepositoryUrl', value: config.repositoryUrl ), 
            string(name: 'CheckoutDirectory', value: config.checkoutDirectory ), 
            string(name: 'BuildSlaveTag', value: config.buildSlaveTag ), 
            string(name: 'AdditionalGenerateArguments', value: config.additionalGenerateArguments ), 
            string(name: 'AdditionalBuildArguments', value: config.additionalBuildArguments )
            ] ,
            quietPeriod: 0 ,
            wait: false
    }
}



