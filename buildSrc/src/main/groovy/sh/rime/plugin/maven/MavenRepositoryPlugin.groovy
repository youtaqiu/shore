package sh.rime.plugin.maven

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin


/**
 * @author youta
 * */
abstract class MavenRepositoryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply(MavenPublishPlugin.class)
        def publishing = project.extensions.getByType(PublishingExtension.class)
        publishing.repositories.mavenLocal()
        try {
            publishing.repositories.maven { maven ->
                maven.setAllowInsecureProtocol(true)
                maven.url = project.layout.buildDirectory.dir('staging-deploy')
            }
        } catch (Exception ignored) {

        }
    }

}
