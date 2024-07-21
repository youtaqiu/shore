package sh.rime.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformExtension
import org.gradle.api.plugins.JavaPlatformPlugin
import sh.rime.plugin.maven.MavenCentralPlugin

/**
 * @author youta
 * */
@SuppressWarnings("unused")
class BomPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply(JavaPlatformPlugin.class)
        project.pluginManager.apply(MavenCentralPlugin.class)
        project.extensions.getByType(JavaPlatformExtension.class).allowDependencies()
    }
}
