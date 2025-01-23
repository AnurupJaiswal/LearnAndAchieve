package com.anurupjaiswal.learnandachieve.model
data class ModuleResponse(
    val message: String,
    val subject_name: String,
    val subject_id: String,
    val medium: String,
    val moduleList: List<Module>,
    val availableDataCount: Int
)

data class Module(
    val module_id: String,
    val module_name: String
)