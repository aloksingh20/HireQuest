package com.example.gethired.api.retrofitInterface

import com.example.gethired.Token.LoginResponse
import com.example.gethired.entities.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST
    fun getOtp(@Body email:String):Call<OtpResponse>

    @POST("create-user")
    fun createUser(@Body registerDto: RegisterDto):Call<RegistrationResponse>

    @PUT("{userId}/update-user")
    fun updateUser(@Body userDto: UserDto,@Path("userId") userId:Long):Call<UserDto>

    @POST("user-login")
    fun loginUser(@Body loginDto: LoginDto):Call<LoginResponse>

    @POST("google-sign-in")
    fun googleSignIn(@Body loginDto: GoogleSignInClass):Call<LoginResponse>

    @PUT("user-logout")
    fun logout(@Query("username") username: String): Call<Boolean>

    @GET("{token}/get-user-info")
    fun getUser(@Path("token") token:String):Call<UserDto>

    @GET("get-userBy-username")
    fun getUserInfo(@Query("username") username: String):Call<UserDto>

    @GET("get-userBy-userId")
    fun getUserInfoById(@Query("userId") userId: Long):Call<UserDto>

    @GET("{username}/get-fcm-token")
    fun getFcmToken(@Path("username") username:String):Call<Boolean>

    @GET("update-fcm-token")
    fun updateFcmToken(@Query("username") username:String,@Body fcmToken:String):Call<Boolean>

    @GET("userProfiles")
    fun getAllUserProfile(@Query("query") newText: String, @Query("role") role:String, @Query("pageNo") pgeNo:Int, @Query("pageSize") pageSize:Int, @Query("sortBy") sortBy:String,@Query("sortDir") sortDir:String):Call<List<String>>

    @GET("{username}/check-username")
    fun checkIfUserIsPresent(@Path("username") username: String):Call<Boolean>

    @GET("{email}/check-email")
    fun checkIfEmailIsPresent(@Path("email") email: String):Call<Boolean>

    @GET("{userId}/get-user-profile")
    fun getCandidateProfile(@Path("userId") userId:Long):Call<UserProfileDto>

    @PUT("{userProfileId}/update-user-profile")
    fun updateUserProfile(
        @Path("userProfileId") userProfileId: Long,
        @Body userProfileDto: UserProfileDto
    ):Call<UserProfileDto>


    //     education
    @POST("{userProfileId}/add-education")
    fun addEducation(@Body education: Education, @Path("userProfileId") userProfileId:Long): Call<Education>

    @PUT("{educationId}/update-education")
    fun updateEducation(@Body education: Education, @Path("educationId") educationId:Long): Call<Education>

    @GET("{userProfileId}/get-educations")
    fun getAllEducation(@Path("userProfileId")userProfileId: Long) :Call<List<Education>>

    @DELETE("{educationId}/delete-education")
    fun deleteEducation(@Path("educationId") educationId: Long): Call<Boolean>
//     Address

    @POST("{userProfileId}/add-address")
    fun addAddress(@Body address: Address, @Path("userProfileId") userProfileId:Long): Call<Address>

//    Award

    @POST("{userProfileId}/add-appreciation")
    fun addAppreciation(@Body award: Appreciation, @Path("userProfileId") userProfileId:Long):Call<Appreciation>

    @PUT("{appreciationId}/update-appreciation")
    fun updateAppreciation(@Body appreciation: Appreciation, @Path("appreciationId") appreciationId:Long):Call<Appreciation>

    @GET("{userProfileId}/get-appreciations")
    fun getAllAppreciation(@Path("userProfileId") userProfileId: Long):Call<List<Appreciation>>

    @DELETE("{appreciationId}/delete-appreciation")
    fun deleteAppreciation(@Path("appreciationId") appreciationId: Long) :Call<Boolean>

//    Certificate

    @POST("{userProfileId}/add-certificate")
    fun addCertificate(@Body certificate: Certificate,@Path("userProfileId")userProfileId:Long):Call<Certificate>

    @PUT("{certificateId}/update-certificate")
    fun updateCertificate(@Path("certificateId") certificateId:Long,@Body certificate: Certificate):Call<Certificate>

    @DELETE("{certificateId}/delete-certificate")
    fun deleteCertificate(@Path("certificateId") certificateId: Long):Call<Boolean>

    @GET("{certificateId}/get-certificate")
    fun getCertificate(@Path("certificateId") certificateId: Long):Call<Certificate>

    @GET("{userProfileId}/get-certificates")
    fun getAllCertificate(@Path("userProfileId") userProfileId:Long):Call<List<Certificate>>

//  Experience

    @POST("{userProfileId}/add-experience")
    fun addExperience(@Body experience: Experience, @Path("userProfileId") userProfileId:Long):Call<Experience>

    @PUT("{experienceId}/update-experience")
    fun updateExperience(@Path("experienceId") experienceId:Long,@Body experience: Experience):Call<Experience>

    @DELETE("{experienceId}/delete-experience")
    fun deleteExperience(@Path("experienceId") experienceId: Long):Call<Boolean>

    @GET("{experienceId}/get-experience")
    fun getExperience(@Path("experienceId") experienceId: Long):Call<Experience>

    @GET("{userProfileId}/get-experiences")
    fun getAllExperience(@Path("userProfileId") userProfileId:Long):Call<List<Experience>>

//    language

    @POST("{userProfileId}/add-languages")
    fun addLanguage(@Body languages:List<String>, @Path("userProfileId") userProfileId:Long):Call<List<String>>

    @GET("{userProfileId}/get-languages")
    fun getAllLanguage(@Path("userProfileId") userProfileId:Long):Call<List<String>>

//    project

    @POST("{userProfileId}/add-project")
    fun addProject(@Body project: Project, @Path("userProfileId") userProfileId:Long):Call<Project>

    @PUT("{projectId}/update-project")
    fun updateProject(@Body project: Project,@Path("projectId") projectId:Long):Call<Project>

    @DELETE("{projectId}/delete-project")
    fun deleteProject(@Path("projectId") projectId:Long):Call<Boolean>

    @GET("{userProfileId}/get-projects")
    fun getAllProject(@Path("userProfileId") userProfileId:Long):Call<List<Project>>

    @GET("{projectId}/get-project")
    fun getProject(@Path("projectId") projectId: Long):Call<Project>


//  skill

    @POST("{userProfileId}/add-skills")
    fun addSkill(@Body skill: List<String>, @Path("userProfileId") userProfileId:Long):Call<List<String>>

    @GET("{userProfileId}/get-skills")
    fun getAllSkill(@Path("userProfileId") userProfileId:Long):Call<List<String>>

//    hobbies
    @POST("{userProfileId}/add-hobbies")
    fun addHobbies(@Body skill: List<String>, @Path("userProfileId") userProfileId:Long):Call<List<String>>

    @GET("{userProfileId}/get-hobbies")
    fun getAllHobbies(@Path("userProfileId") userProfileId:Long):Call<List<String>>

//    profileLink
    @POST("{userProfileId}/add-profile")
    fun addProfileLink(@Body profile: Profile, @Path("userProfileId") userProfileId:Long):Call<Profile>

    @GET("{userProfileId}/get-profiles")
    fun getAllProfileLink(@Path("userProfileId") userProfileId:Long):Call<List<Profile>>

    @PUT("{profileLinkId}/update-profile")
    fun updateProfileLink(@Body profile: Profile, @Path("profileLinkId") profileLinkId:Long):Call<Profile>

    @GET("{profileLinkId}/get-profile")
    fun getProfileLink(@Path("profileLinkId") profileLinkId:Long):Call<Profile>

    @DELETE("{profileLinkId}/delete-profile")
    fun deleteProfileLink(@Path("profileLinkId") profileLinkId:Long):Call<Boolean>

    // notification

    @POST("send-notification")
    fun sendNotification(@Body request : NotificationRequest):Call<Boolean>

    @PUT("{notificationId}/update-notification")
    fun updateNotification(@Path("notificationId") notificationId:Long):Call<Notification>

    @DELETE("{notificationId}/delete-notification")
    fun deleteNotification(@Path("notificationId") notificationId: Long):Call<Boolean>

    @GET("get-notifications")
    fun getNotification(@Query("receiverUsername") receiverUsername:String):Call<List<Notification>>


    @GET("notificationPreferences")
    fun getNotificationPreference(@Query("userId") userId: Long):Call<List<NotificationPreference>>

    @POST("{notificationType}/mute")
    fun updateNotificationPreference(@Path("notificationType") notificationType:String, @Query("userId") userId: Long):Call<Boolean>

    @PUT("muteAllNotification")
    fun muteAllNotification(@Query("userId") userId: Long):Call<Boolean>

    @PUT("unMuteAllNotification")
    fun unMuteAllNotification(@Query("userId") userId: Long):Call<Boolean>

//    pdf
    @Multipart
    @POST("{userProfileId}/add-pdf")
    fun addPdf(@Path("userProfileId") userProfileId:Long, @Part file: MultipartBody.Part): Call<Pdf>

    @DELETE("{pdfId}/delete-pdf")
    fun deletePdf(@Path("pdfId") pdfId:Long):Call<Boolean>

    @GET("{userProfileId}/get-pdfs")
    fun getAllPdf(@Path("userProfileId") userProfileId:Long):Call<List<Pdf>>

    @GET("{pdfId}/download-pdf")
    fun downloadPdf(@Path("pdfId") pdfId: Long): Call<ResponseBody>

//    otp - email
    @POST("{email}/send-otp")
    fun sendOtp(@Path("email") email:String):Call<OtpResponse>

    @POST("{otpCode}/{email}/otp-verification")
    fun verifyOtp(@Path("otpCode") otpCode:String, @Path("email") email: String):Call<Response>

    @PUT("{email}/{password}/change-password")
    fun updatePassword(@Path("email") email:String,@Path("password") password:String): Call<UserDto>

//    upload-profile-img
    @Multipart
    @POST("upload-image")
    fun uploadImage(@Query("userId") userId: Long, @Part file: MultipartBody.Part): Call<ResponseBody>

    @GET("get-image")
    fun getUserProfilePicture(@Query("userId") userId: Long):Call<ResponseBody>

//    chat

    @GET("{senderId}/{receiverId}/{timeStamp}/get-message")
    fun getChat(@Path("senderId") senderId:Long, @Path("receiverId") receiverId:Long,@Path("timeStamp") timeStamp: String): Call<List<Chat>>

    @PUT("{senderId}/{receiverId}/update-chat-message")
    fun updateChatMessage(@Path("senderId") senderId:Long, @Path("receiverId") receiverId:Long,@Body chat: Chat): Call<Chat>

//  chat room

    @POST("{senderId}/send-chat-request")
    fun sendChatRequest(@Path("senderId") senderId:Long, @Query("receiverId") receiverId: Long, @Body sampleMessage: Chat): Call<Boolean>

    @GET("{userId}/get-chatting-list")
    fun getAllChattingList(@Path("userId") userId: Long):Call<List<ChatRoom>>

//    chatting - screen
    @GET("{senderId}/{receiverId}/get-user-info")
    fun getChattingUserInfo(@Path("senderId") senderId:Long, @Path("receiverId") receiverId: Long):Call<ChattingUserInfo>

    @PUT("accept-chatRequest")
    fun acceptChatRequest(@Query("chatRoomId") chatRoomId:Long):Call<Boolean>

    @PUT("delete-chatRequest")
    fun deleteChatRequest(@Query("chatRoomId") chatRoomId:Long):Call<Boolean>



//    recent search

    @POST("add-recentSearch")
    fun addRecentSearch(@Body recentSearch: RecentSearch):Call<RecentSearch>

    @GET("{userId}/get-recentSearches")
    fun getAllRecentSearch(@Path("userId") userId: Long): Call<List<RecentSearch>>

    @DELETE("{recentSearchId}/delete-recentSearch")
    fun deleteRecentSearch(@Path("recentSearchId") recentSearchId: Long): Call<Boolean>

    @DELETE("{userId}/delete-recentSearches")
    fun deleteAllRecentSearch(@Path("userId") userId: Long): Call<Boolean>


//    meeting

    @POST("create-meeting")
    suspend fun createMeeting(@Body meeting:Meeting): retrofit2.Response<Meeting>

    @GET("{user}/upcoming-meetings")
    suspend fun getAllMeeting(@Path("user") user:String): retrofit2.Response<List<Meeting>>

    @GET("{user}/past-meetings")
    suspend fun getAllPastMeeting(@Path("user") user:String): retrofit2.Response<List<Meeting>>

    @PUT("{meetingId}/update-meeting")
    suspend fun updateMeeting(@Path("user") user:String, @Path("hr") hr:String, @Body meeting:Meeting): retrofit2.Response<Meeting>

// bookmark user profile
    @PUT("{hrId}/add-bookmark-profile")
    fun addBookmarkProfile(@Path("hrId") hrId:Long, @Query("userProfileId") userProfileId: Long):retrofit2.Response<Boolean>

    @DELETE("{hrId}/add-bookmark-profile")
    fun removeBookmarkProfile(@Path("hrId") hrId:Long, @Query("userProfileId") userProfileId: Long):retrofit2.Response<Boolean>

    @GET("get-bookmark-profile")
    fun getBookmarkProfiles(@Query("hrId") hrId: Long):retrofit2.Response<List<UserProfile>>
}