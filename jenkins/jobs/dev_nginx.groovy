multibranchPipelineJob('Dev/nginx-deployment') {
    branchSources {
        github {
            scanCredentialsId('aamirshehzad111')
            repository('nginx-deployment')
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
        it / 'triggers' << 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
            spec '* * * * *'
            interval "60000"
        }
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
