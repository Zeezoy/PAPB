package com.example.navigationlab.data.source

import com.example.navigationlab.data.model.Student

class StudentLocalDataSourceLegacy {
    private val students = listOf(
        Student(2026, "Alya Putri", "alya@example.com", "Universitas Brawijaya"),
        Student(2027, "Bima Pratama", "bima@example.com", "Universitas Brawijaya"),
        Student(2028, "Citra Lestari", "citra@example.com", "Universitas Brawijaya"),
        Student(2029, "Danu Setiawan", "danu@example.com", "Universitas Brawijaya"),
        Student(2030, "Endah Maharani", "endah@example.com", "Universitas Brawijaya")
    )

    fun getStudents(): List<Student> = students

    fun getStudentById(id: Int): Student? =
        students.find { it.id == id }
}