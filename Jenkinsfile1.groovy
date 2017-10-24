#!groovy

/**
This is a .groovy script that starts builds of the googletest-PackageTest project for multiple configurations.
*/

stage('Trigger Builds')
{
    def parallelNodes = [:]
    //parallelNodes.failFast = true
    
    def toolchains = [Windows-10,Windows-10,Windows-10,Windows-10]
    
    // add nodes for building the pipeline
    for(toolchain in toolchains)
    {
        echo "Create build node " + toolchain
        def myNode = createBuildNode(toolchain)
        parallelNodes[toolchain] = myNode
    }

    // run the nodes
    parallel parallelNodes
}

def createBuildNode(toolchain)
{
    return { 
        node(toolchain) 
        {
            echo "Woohaaa ran the ${toolchain} node"
        }
    }
}