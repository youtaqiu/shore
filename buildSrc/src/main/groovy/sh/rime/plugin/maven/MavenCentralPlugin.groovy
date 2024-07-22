//package sh.rime.plugin.maven
//
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.api.plugins.JavaPlugin
//import org.jreleaser.gradle.plugin.JReleaserExtension
//import org.jreleaser.gradle.plugin.JReleaserPlugin
//
///**
// * @author youta
// * */
//@SuppressWarnings("unused")
//class MavenCentralPlugin implements Plugin<Project> {
//
//    @Override
//    void apply(Project project) {
//        project.pluginManager.apply(DeployedPlugin.class)
//        project.pluginManager.apply(JReleaserPlugin.class)
//        def jExtension = project.extensions.getByType(JReleaserExtension.class)
//        def rootProject = project.rootProject
//        def projectDir = rootProject.layout.projectDirectory
//        def publicPgp = projectDir.file('.jreleaser/gpg/public.pgp')
//        def privatePgp = projectDir.file('.jreleaser/gpg/private.pgp')
//        if (!project.layout.buildDirectory.get().asFile.toPath().resolve('jreleaser').toFile().exists()){
//            project.layout.buildDirectory.get().asFile.toPath().resolve('jreleaser').toFile().mkdirs()
//        }
//        jExtension.signing {
//            active = 'ALWAYS'
//            armored = true
//            mode = 'FILE'
//            publicKey = publicPgp.toString()
//            secretKey = privatePgp.toString()
//        }
//        jExtension.gitRootSearch.set(true)
////        jExtension.catalog {
////            active = 'ALWAYS'
////            sbom {
////                enabled = true
////            }
////        }
//        project.plugins.withType(JavaPlugin.class).every {
//            jExtension.deploy {
//                maven {
//                    mavenCentral {
//                        sonatype {
//                            active = 'ALWAYS'
//                            url = 'https://central.sonatype.com/api/v1/publisher'
//                            var file = project.layout.buildDirectory.dir('staging-deploy')
//                            stagingRepository(file.get().toString())
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//
//}