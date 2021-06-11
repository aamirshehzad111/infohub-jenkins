multibranchPipelineJob('Prod/Insurance') {
    branchSources {
        github {
            scanCredentialsId('githiub-token')
            repository('insurance_backend')
            repoOwner('spartans111')
            buildForkPRHead(false)
            buildForkPRMerge(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            id('24b0e711-730f-494c-b3aa-34314bd73f3f')
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
