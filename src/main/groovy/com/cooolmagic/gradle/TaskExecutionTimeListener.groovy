package com.cooolmagic.gradle

import org.gradle.BuildAdapter
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class TaskExecutionTimeListener extends BuildAdapter implements TaskExecutionListener {

    class TimelineEntry {
        String module
        String task
        long startTimeMillis
        String startTime
        String endTime
    }

    def timelinePluginExtension;

    def project = null
    def runningTasks = [:]

    TaskExecutionTimeListener(project, timelinePluginExtension) {
        this.project = project
        this.timelinePluginExtension = timelinePluginExtension
        this.timelinePluginExtension.timelineEntries = []
    }

    void beforeExecute(Task task) {
        runningTasks.put(task.path, new TimelineEntry(module: task.project.name, task: task.name, startTimeMillis: System.currentTimeMillis()))
    }

    void afterExecute(Task task, TaskState state) {
        def timelineEntry = runningTasks[task.path]

        def now = System.currentTimeMillis()
        def duration = now - timelineEntry.startTimeMillis

        def include = duration > timelinePluginExtension.thresholdTimeMillis
        timelinePluginExtension.alwaysExclude.each() { name ->
            if (task.path.endsWith(name)) {
                include = false
            }
        }
        timelinePluginExtension.alwaysInclude.each() { name ->
            if (task.path.endsWith(name)) {
                include = true
            }
        }

        if (include && state.getDidWork()) {
            timelineEntry.startTime = getTimeString(timelineEntry.startTimeMillis)
            timelineEntry.endTime = getTimeString(now)
            timelinePluginExtension.timelineEntries << timelineEntry
        }
        runningTasks.remove(task.path)
    }

    String getTimeString(long end) {
        def durationSecs = (int) (end - project.startTime) / 1000
        def hr = 12 + (int) (durationSecs / (60 * 60))
        def min = (int) (durationSecs / 60)
        def sec = durationSecs % 60
        return "$hr, $min, $sec"
    }
}
