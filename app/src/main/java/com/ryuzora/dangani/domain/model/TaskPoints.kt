package com.ryuzora.dangani.domain.model

/**
 * Task points based on Fibonacci sequence, inspired by Jira story points.
 * Each point value has an estimated Rupiah cost.
 */
enum class TaskPoints(val value: Int, val estimatedCost: String) {
    ONE(1, "~Rp. 11.000"),
    TWO(2, "~Rp. 20.000"),
    THREE(3, "~Rp. 50.000"),
    FIVE(5, "~Rp. 100.000"),
    EIGHT(8, "~Rp. 250.000"),
    THIRTEEN(13, "Rp. 500.000>");

    companion object {
        fun fromValue(value: Int): TaskPoints? = entries.find { it.value == value }
    }
}
