package sh.rime.plugin.compile

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin

/**
 * @author youta
 * */
abstract class ResourcesPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.pluginManager.apply(JavaLibraryPlugin.class)
        project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME).get()
                .dependsOn(JavaPlugin.PROCESS_RESOURCES_TASK_NAME)
    }

}
