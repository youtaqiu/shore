package sh.rime.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import sh.rime.plugin.compile.ResourcesPlugin

/**
 * @author youta
 * */
class CommonPlugin implements Plugin<Project>{

    void apply(Project project) {
        project.pluginManager.apply(JavaLibraryPlugin.class)
        project.pluginManager.apply(ModulePlugin.class)
        project.pluginManager.apply(ResourcesPlugin.class)
    }

}
