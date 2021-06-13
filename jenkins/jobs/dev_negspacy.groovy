multibranchPipelineJob('Dev/Negspacy') {
    branchSources {
        github {
            scanCredentialsId('aamirshehzad111')
            repository('negspacy')
            repoOwner('spartans111')
            buildForkPRHead(false)
            buildForkPRMerge(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(true)
            excludes('master')
            id('df9ff1d6-b020-4461-8e37-123b47c90f5b')
            includes('*')
        }
    }
    configure {
        it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory") << {
            scriptPath("pipelines/dev.groovy")
        }     
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
        }
    }
}