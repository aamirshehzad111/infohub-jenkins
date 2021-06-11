#!/usr/bin/env groovy

@NonCPS
def getEMail(String gitUserEmail) {
    try{
        if(env.BUILD_NUMBER.equals("2")){
            return gitUserEmail
        }else{
            def user = currentBuild.rawBuild.causes[0].userId 
            println "User email to notify to: " + user
            return user
        }
    }catch(Exception ex) {
        println 'git user email is : ' + gitUserEmail
        return gitUserEmail
    }
}

def call(String gitUserEmail) {
 def status, logRegex
 
 def emailID = getEMail(gitUserEmail)
    switch (currentBuild.currentResult) {
        case 'SUCCESS':
            status = 'successed'
            logRegex = 'SUCCESS'
            break

        case 'UNSTABLE':
            status = 'unstable'
            logRegex = 'FAILURE'
            break

        case 'FAILURE':
            status = 'failed'
            logRegex = 'FAILURE'
            break

        case 'ABORTED':
            status = 'canceled'
            logRegex = 'ABORTED'
            break

    }
    if (currentBuild.currentResult != "SUCCESS") {
        emailext(subject: "Build $status - ${JOB_NAME} #${BUILD_NUMBER} ",
            body: """ Job: ${env.JOB_NAME}\n Branch: ${env.BRANCH_NAME}\n Build Number: ${BUILD_NUMBER}\n Build Url: ${BUILD_URL}\n Status: ${currentBuild.currentResult}"""
            , from: '"Jenkins server" <foo@acme.org>'
             , to: "${emailID}")
    }

}