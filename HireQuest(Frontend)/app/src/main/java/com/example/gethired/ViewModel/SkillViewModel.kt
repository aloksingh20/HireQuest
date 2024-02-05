package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.SkillRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult

class SkillViewModel(tokenManager: TokenManager) : ViewModel() {
    private val skillRepository = SkillRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _addedSkills = MutableLiveData<List<String>>(null)
    val addedSkills: LiveData<List<String>> = _addedSkills

    private val _skillList = MutableLiveData<List<String>>(null)
    val skillList: LiveData<List<String>> = _skillList

    private suspend fun <T> executeApiCall(
        call: suspend () -> ApiResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        _loading.value = true
        _error.value = null

        try {
            when (val result = call()) {
                is ApiResult.Success -> onSuccess(result.data!!)
                is ApiResult.Error -> onError(result.exception.localizedMessage ?: "Unknown error")
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Unknown error")
        } finally {
            _loading.value = false
        }
    }
    // Function to fetch all skills for a given userProfileId
    suspend fun getSkills(userProfileId: Long){
        executeApiCall(
            call = { skillRepository.getAllSkills(userProfileId) },
            onSuccess = { _skillList.value = it },
            onError = { _error.value = it }
        )
    }


    // Function to add a new skill
    suspend fun addSkill(skill: List<String>,userProfileId: Long) {
        executeApiCall(
            call = { skillRepository.addSkill(skill,userProfileId) },
            onSuccess = { _addedSkills.value = it },
            onError = { _error.value = it }
        )
    }

}