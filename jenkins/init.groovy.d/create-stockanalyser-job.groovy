import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jobName = 'stockanalyser-ci'
def jenkinsfile = new File('/workspace/cicd-stockanalyser/Jenkinsfile')

if (!jenkinsfile.exists()) {
    println "Skipping ${jobName}: ${jenkinsfile.path} was not found."
    return
}

def jenkins = Jenkins.get()
def job = jenkins.getItem(jobName)

if (job == null) {
    job = jenkins.createProject(WorkflowJob, jobName)
}

job.setDefinition(new CpsFlowDefinition(jenkinsfile.text, true))
job.setDescription('Build, test, and Dockerize the StockAnalyser Flask app.')
job.save()

println "Configured Jenkins pipeline job: ${jobName}"
