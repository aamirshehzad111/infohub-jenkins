def nthLastIndexOf(int nth, String ch, String string) {
    if (nth <= 0) return string.length();
    return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
}
def calculateURL() {
    def URL = "${BUILD_URL}"
    return URL.substring(0, nthLastIndexOf(2, "/", URL)+1) + "buildWithParameters?delay=0sec"
}
def call(String jenkins_user_token) {
    def redirect = "${calculateURL()}"
    sh """
       set +x
       echo "Hello"
       echo "$redirect"
       curl --user "${jenkins_user_token}" -X POST -H "Content-Type: application/json" "${redirect}"
    """
    // Abort the build, skipping subsequent stages
    error("Aborting build: First job run fails because of jenkins parameters of the pipeline are not defined in the job, so it needs to fetch the pipeline the first time.")
}

