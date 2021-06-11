def call(Map pipelineParams) {
pipeline {
    agent {
        label "${pipelineParams.NODE_LABEL}"
    }
    options {
        timeout(time: 90, unit: 'MINUTES')
    }
    environment {
        gitUser = sh(script: "git log -1 --pretty=format:'%an'", , returnStdout: true).trim()
        gitUserEmail = sh(script: "git log -1 --pretty=format:'%ce'", , returnStdout: true).trim()
        jenkinsUserToken = credentials('jenkins-user-token')
    }
    parameters {
        choice(name: 'OPTION', choices: ['test','deploy'], description: 'The build type')
    }
    
    stages {
        stage('To Fail First Build') {
            when {
                expression {
                    return env.BUILD_NUMBER.equals("1")
                }
            }
            steps {
                script {
                    validateParams("${env.jenkinsUserToken}")
                }
            }
        }

        stage (' To stop previous running builds') {
            steps {
                  script {
		            sh "echo 'stopping previous build'"
                    abortPreviousBuild()
                  }
            }
        }

        stage ('Test') {
            steps {
                sh "make compute-short-sha BASH_ENV=${pipelineParams.BASH_ENV}"
                sh "make pull-model ENV=${pipelineParams.ENV}"
                sh "make docker-build-image ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL}"
                sh "make run-unit-tests"
            }
        }
        stage('ECS Build & Scale') {
            when {
                expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev' }
            }
            parallel {
                stage ('Build & Push') {
                    steps {
                        sh "make publish ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL}"
                    }
                } 
                stage ('ECS Scale Out') {
                    steps {
                        sh "make ecs-scaling-out ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL} REGION=${pipelineParams.AWS_REGION} ROBERTA_ASG=${pipelineParams.ROBERTA_ASG} CAPACITY_FOR_DEPLOYMENT=${pipelineParams.CAPACITY_FOR_DEPLOYMENT} SERVICE_ROBERTA=${pipelineParams.SERVICE_ROBERTA} CLUSTER_ROBERTA=${pipelineParams.CLUSTER_ROBERTA}"
                    }
                }
            }
        }
        stage ('ECS Deploy') {
            when {
                expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev'}
            }
            steps {
                sh "make ecs-deploy ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL} REGION=${pipelineParams.AWS_REGION} SERVICE_ROBERTA=${pipelineParams.SERVICE_ROBERTA} CLUSTER_ROBERTA=${pipelineParams.CLUSTER_ROBERTA}"
            }

        }
    }
    post {
     always {
       sh "make ecs-scaling-in ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL} REGION=${pipelineParams.AWS_REGION} ROBERTA_ASG=${pipelineParams.ROBERTA_ASG} CAPACITY_NORMAL=${pipelineParams.CAPACITY_NORMAL} SERVICE_ROBERTA=${pipelineParams.SERVICE_ROBERTA} CLUSTER_ROBERTA=${pipelineParams.CLUSTER_ROBERTA}"
       slacknotifier("${env.gitUser}")
       sendEmail("${env.gitUserEmail}")
     }
   }
}
}