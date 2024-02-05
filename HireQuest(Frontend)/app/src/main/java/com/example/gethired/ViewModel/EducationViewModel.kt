
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gethired.Repository.EducationRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.api.ApiResult
import com.example.gethired.entities.Certificate
import com.example.gethired.entities.Education

class EducationViewModel(tokenManager: TokenManager) : ViewModel() {
    private val educationRepository= EducationRepository(tokenManager)

    // LiveData for loading state and errors
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // LiveData for API responses
    private val _createdEducation = MutableLiveData<Education>()
    val createdEducation: LiveData<Education> = _createdEducation

    private val _updatedEducation = MutableLiveData<Education>()
    val updatedEducation: LiveData<Education> = _updatedEducation

    private val _educationList = MutableLiveData<List<Education>>()
    val educationList : LiveData<List<Education>> = _educationList

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
    suspend fun addEducation(education: Education,userProfileId:Long){
        executeApiCall(
            call = { educationRepository.addEducation(userProfileId,education) },
            onSuccess = { _createdEducation.value = it },
            onError = { _error.value = it }
        )
    }
    suspend fun getAllEducation(userProfileId:Long){
        executeApiCall(
            call = { educationRepository.getAllEducation(userProfileId) },
            onSuccess = { _educationList.value = it },
            onError = { _error.value = it }
        )
    }
    suspend fun deleteEducation(educationId:Long){
         educationRepository.deleteEducation(educationId)
    }
    suspend fun updateEducation(education:Education,educationId:Long){
        executeApiCall(
            call = { educationRepository.updateEducation(education,educationId) },
            onSuccess = { _createdEducation.value = it },
            onError = { _error.value = it }
        )
    }

}