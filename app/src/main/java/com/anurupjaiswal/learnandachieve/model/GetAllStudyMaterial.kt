package com.anurupjaiswal.learnandachieve.model

class GetAllStudyMaterial {
    var message: String = ""
    var data: List<StudyMaterial> = listOf()
    var availableDataCount: Int = 0
}

class StudyMaterial {
    var subject_id: String = ""
    var subject_name: String = ""
    var color_code: String = ""
    var subject_image: String = ""
    var studyMaterial_id: String = ""
    var class_id: String = ""
    var class_name: String = ""
    var medium: String = ""
}
