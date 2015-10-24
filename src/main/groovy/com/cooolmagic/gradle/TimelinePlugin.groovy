package com.cooolmagic.gradle

import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.Plugin

class TimelinePlugin implements Plugin<Project> {

    def startTimeMillis = System.currentTimeMillis()

    void apply(Project project) {
        project.extensions.create("timeline", TimelinePluginExtension)
        project.timeline.reportsDir = "${project.buildDir}/reports/timeline"

        project.afterEvaluate {
            if (project.timeline.enabled) {
                project.gradle.addListener(new TaskExecutionTimeListener(project, project.timeline))
                project.gradle.buildFinished { BuildResult buildResult ->
                    project.logger.info "Generating timeline report ..."

                    project.delete project.timeline.reportsDir
                    project.mkdir project.timeline.reportsDir

                    def duration = (int) (System.currentTimeMillis() - startTimeMillis) / 1000

                    def hr = (int) (duration / (60 * 60))
                    hr = (hr > 1) ? "$hr hrs " : (hr > 0) ? "$hr hr " : ""

                    def min = (int) (duration / 60)
                    min = (min > 1) ? "$min mins " : (min > 0) ? "$min min " : ""

                    def sec = "${duration % 60} sec"
                    def totalTime = "$hr$min$sec"

                    project.copy {
                        from project.resources.text.fromArchiveEntry(
                                project.buildscript.configurations.classpath.findAll {
                                    it.name.contains 'gradle-timeline-plugin'
                                },
                                'templates/timeline.html.template').asFile()
                        into project.timeline.reportsDir
                        fileMode = 0554

                        rename { file -> 'index.html' }
                        expand(entries: project.timeline.timelineEntries, totalTime: totalTime, thresholdTimeMillis: project.timeline.thresholdTimeMillis)
                    }
                }
            }
        }
    }
}
