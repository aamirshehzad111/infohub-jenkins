multibranchPipelineJob('Dev/Roberta') {
    branchSources {
        github {
            scanCredentialsId('githiub-token')
            repository('roberta')
            repoOwner('spartans111')
            buildForkPRHead(false)
            buildForkPRMerge(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(true)
            excludes('master')
            id('afba5e57-e4ec-441e-8371-a265ba241fb7')
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
