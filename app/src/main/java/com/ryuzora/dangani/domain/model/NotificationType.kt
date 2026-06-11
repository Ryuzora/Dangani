package com.ryuzora.dangani.domain.model

enum class NotificationType(val displayName: String) {
    NEW_APPLICANT("Pelamar Baru!"),
    WORK_SUBMITTED("Tugas Telah Dikirim"),
    APPLICATION_ACCEPTED("Lamaran Diterima!"),
    APPLICATION_NOT_SELECTED("Lamaran Belum Dipilih"),
    WORK_ACCEPTED("Pekerjaan Disetujui"),
    WORK_REVISION("Pekerjaan Revisi!"),
    RATING_RECEIVED("Rating Diterima")
}
