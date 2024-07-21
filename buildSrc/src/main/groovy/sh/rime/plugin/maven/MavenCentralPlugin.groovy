package sh.rime.plugin.maven

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jreleaser.gradle.plugin.JReleaserExtension
import org.jreleaser.gradle.plugin.JReleaserPlugin

/**
 * @author youta
 * */
@SuppressWarnings("unused")
class MavenCentralPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply(DeployedPlugin.class)
        project.pluginManager.apply(JReleaserPlugin.class)
        def jExtension = project.extensions.getByType(JReleaserExtension.class)
        jExtension.deploy {
            maven {
                mavenCentral {
                    sonatype {
                        active = 'ALWAYS'
                        url = 'https://central.sonatype.com/api/v1/publisher'
                        stagingRepository('target/staging-deploy')
                    }
                }
            }
        }
    }

}