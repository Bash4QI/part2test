import com.lagradost.cloudstream3.gradle.CloudstreamExtension

val repoUrl = "https://github.com/Bash4QI/part2test"

cloudstream {
    setRepo(repoUrl)
    authors = listOf("Bash4QI")
}

