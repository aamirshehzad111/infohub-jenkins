multibranchPipelineJob('Prod/Negspacy') {
    branchSources {
        github {
            scanCredentialsId('githiub-token')
            repository('negspacy')
            repoOwner('spartans111')
            buildForkPRHead(false)
            buildForkPRMerge(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            id('84807f33-5108-4578-8009-b0b513cc66d8')
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
