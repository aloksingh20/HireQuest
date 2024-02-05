package com.example.gethired.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gethired.entities.Image
import com.example.gethired.Repository.ImageRepository
import com.example.gethired.Token.TokenManager
import com.example.gethired.exception.ResponseException
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ImageViewModel(tokenManager: TokenManager) : ViewModel() {

    private val imageRepository = ImageRepository(tokenManager)

    // LiveData for image responses (no longer nullable)
    private val _imageResponse = MutableLiveData<ResponseBody>()
    val imageResponse: LiveData<ResponseBody> = _imageResponse

    // LiveData for errors
    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    fun addProfileImage(userId: Long, file: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val image = imageRepository.addProfileImage(userId, file)
                _imageResponse.postValue(image)
            } catch (e: ResponseException) {
                // Handle image-specific error
                _error.postValue(e)
            } catch (e: Exception) {
                // Handle other general errors
                _error.postValue(e)
            }
        }
    }

    fun getUserProfilePicture(userId: Long) {
        viewModelScope.launch {
            try {
                val image = imageRepository.getUserProfilePicture(userId)

                _imageResponse.postValue(image)
            } catch (e: Exception) {
                _error.postValue(e)
            }
        }
    }
}
