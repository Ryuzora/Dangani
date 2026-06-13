package com.ryuzora.dangani.domain.model

enum class TaskStatus(val displayName: String) {
    UNASSIGNED("Unassigned"),
    IN_PROGRESS("In Progress"),
    NEED_REVIEW("Need Review"),
    REVISION("Revision"),
    ACCEPTED("Accepted")
}

