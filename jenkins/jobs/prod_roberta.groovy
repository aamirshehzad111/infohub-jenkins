multibranchPipelineJob('Prod/Roberta') {
    branchSources {
        github {
            scanCredentialsId('aamirshehzad111')
            repository('roberta')
            repoOwner('spartans111')
            buildForkPRHead(false)
            buildForkPRMerge(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            id('384c0f92-c78f-11ea-87d0-0242ac130003')
            includes('master')
        }
    }
    configure {
        it / factory(class: "org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory") << {
            scriptPath("pipelines/prod.groovy")
        }     
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
        }
    }
}

