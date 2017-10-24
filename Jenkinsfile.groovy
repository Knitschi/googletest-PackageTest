#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/

stage('Trigger Builds')
{
    build job: 'CMakeProjectBuildJob', parameters: [
        string(name: 'RepositoryUrl', value: 'https://github.com/Knitschi/googletest-PackageTest.git'), 
        string(name: 'Googletest-VS2015-static-debug', value: 'GoogleTestPackageTest'), 
        string(name: 'BuildSlaveTag', value: 'Windows-10'), 
        string(name: 'AdditionalGenerateArguments', value: '-G"Visual Studio 14 2015"'), 
        string(name: 'AdditionalBuildArguments', value: '--config Debug')
        ]
    
    build job: 'CMakeProjectBuildJob', parameters: [
        string(name: 'RepositoryUrl', value: 'https://github.com/Knitschi/googletest-PackageTest.git'), 
        string(name: 'Googletest-VS2015-static-release', value: 'GoogleTestPackageTest'), 
        string(name: 'BuildSlaveTag', value: 'Windows-10'), 
        string(name: 'AdditionalGenerateArguments', value: '-G"Visual Studio 14 2015"'), 
        string(name: 'AdditionalBuildArguments', value: '--config Release')
        ]
}
