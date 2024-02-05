package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.ProjectRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Profile
import com.example.gethired.entities.Project

class ProjectViewModel(tokenManager: TokenManager) : ViewModel() {
    private val projectRepository= ProjectRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdProject = MutableLiveData<Project>(null)
    val createdProject: LiveData<Project> = _createdProject

    private val _updatedProject = MutableLiveData<Project>(null)
    val updatedProject: LiveData<Project> = _updatedProject

    private val _projectList = MutableLiveData<List<Project>?>(null)
    val projectList: LiveData<List<Project>?> = _projectList

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

    suspend fun addProject(project: Project, userProfileId:Long){
        executeApiCall(
            call = { projectRepository.addProject(userProfileId ,project) },
            onSuccess = { _createdProject.value = it },
            onError = { _error.value = it }

        )
    }

    suspend fun updateProject(project: Project, projectId: Long){
        executeApiCall(
            call = { projectRepository.updateProject(projectId ,project) },
            onSuccess = { _updatedProject.value = it },
            onError = { _error.value = it }

        )
    }
    suspend fun getAllProject(userProfileId: Long) {
        executeApiCall(
            call = { projectRepository.getAllProject(userProfileId ) },
            onSuccess = { _projectList.value = it },
            onError = { _error.value = it }

        )
    }

    suspend fun deleteProject(projectId: Long){
        projectRepository.deleteProject(projectId)
    }
}
