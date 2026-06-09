package com.ryuzora.dangani.domain.model

enum class TaskCategory(val displayName: String) {
    ALL("All"),
    ACADEMICS("Academics"),
    DESIGN("Design"),
    PHYSICS("Physics"),
    IT("IT"),
    PHYSICAL("Physical"),
    ENTERTAINMENT("Entertainment"),
    ACADEMIC_WRITING("Academic Writing"),
    ENGINEERING("Engineering"),
    SCIENCE("Science");

    companion object {
        /**
         * Semua kategori selectable untuk filtering dan creation
         * (excludes ALL)
         */
        fun getSelectableCategories(): List<TaskCategory> {
            return entries.filter { it != ALL }
        }

        /**
         * Semua kategori termasuk ALL untuk Home filter
         */
        fun getCategoriesWithAll(): List<TaskCategory> {
            return listOf(ALL) + getSelectableCategories()
        }
    }
}

