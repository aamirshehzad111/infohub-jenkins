import jenkins.model.JenkinsLocationConfiguration
import hudson.model.Descriptor
jlc = JenkinsLocationConfiguration.get()
jlc.setUrl("http://build.test-something.com/") 
jlc.save()
println(jlc.getUrl())