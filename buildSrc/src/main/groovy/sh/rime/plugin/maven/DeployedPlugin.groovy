package sh.rime.plugin.maven

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar

/**
 * @author youta
 * */
abstract class DeployedPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def publication = publication(project)
        project.afterEvaluate { evaluated ->
            project.plugins.withType(JavaPlugin.class).every {
                if ((project.tasks.named(JavaPlugin.JAR_TASK_NAME).get() as Jar).isEnabled()) {
                    def javaPluginExtension = project.extensions.getByType(JavaPluginExtension.class)
                    javaPluginExtension.withSourcesJar()
                    javaPluginExtension.withJavadocJar()
                    project.components
                            .matching { softwareComponent -> softwareComponent.name == "java" }
                            .every { publication.from(it) }
                    mavenInfo(publication, project)
                }
            }
        }
        project.plugins.withType(JavaPlatformPlugin.class).every {
            project.components
                    .matching { softwareComponent -> softwareComponent.name == "javaPlatform" }
                    .every { publication.from(it) }
            mavenInfo(publication, project)
        }
    }

    static MavenPublication publication(Project project) {
        project.pluginManager.apply(MavenPublishPlugin.class)
        project.pluginManager.apply(MavenRepositoryPlugin.class)
        def publishing = project.extensions.getByType(PublishingExtension.class)
        return publishing.publications.create("maven", MavenPublication.class)
    }

    static void mavenInfo(MavenPublication publication, Project project) {
        publication.versionMapping {
            allVariants {
                fromResolutionResult()
            }
        }
        publication.pom {
            name.set(project.name)
            def projectDescription = project.description
            if (projectDescription == null || projectDescription.isBlank()) {
                projectDescription = project.name.replaceAll("-", " ")
            }
            description.set(projectDescription)
            def rootProjectName = project.rootProject.name
            url.set("https://github.com/youtaqiu/" + rootProjectName + "/")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    name.set("rime")
                    email.set("k@rime.sh")
                }
            }
            scm {
                connection.set("git@github.com:youtaqiu/" + rootProjectName + ".git")
                url.set("https://github.com/youtaqiu/" + rootProjectName + "/")
            }
        }
    }

}
