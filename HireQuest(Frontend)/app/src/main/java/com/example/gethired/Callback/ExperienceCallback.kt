package com.example.gethired.Callback

import com.example.gethired.entities.Experience

interface ExperienceCallback {
    fun onExperienceResponse(experience: Experience)
    fun onExperienceError()
}