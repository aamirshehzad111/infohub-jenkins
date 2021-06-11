def jobname = 'jenkins_master_build'
pipelineJob(jobname) {

  logRotator {
      numToKeep(5)
  }
  parameters {
    gitParam('GIT_BRANCH_NAME') {
        description 'The Git branch to checkout'
        type 'BRANCH'
        defaultValue 'origin/master'
        sortMode 'ASCENDING_SMART'
    }
  }
  definition {
    cpsScm {
      scm {
        git {
          remote {
                url('https://github.com/aamirshehzad111/infohub-jenkins.git')
                credentials('githiub-token')
            }
            branches('${GIT_BRANCH_NAME}')
        }
      }
      scriptPath('jenkins/Jenkinsfile')
      lightweight(false)
    }
  }
}