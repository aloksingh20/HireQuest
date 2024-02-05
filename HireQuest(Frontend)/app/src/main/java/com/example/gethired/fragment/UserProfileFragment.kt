package com.example.gethired.fragment

import EducationViewModel
import ExperienceAdapter
import ProjectAdapter
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.Callback.*
import com.example.gethired.ChattingActivity
import com.example.gethired.CommonFunction
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.PREF_FILE_NAME
import com.example.gethired.R
import com.example.gethired.SettingActivity
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.*
import com.example.gethired.adapter.*
import com.example.gethired.entities.*
import com.example.gethired.factory.*
import com.example.gethired.adapter.dynamicSpan.DynamicSpanSizeLookupSkill
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.utils.Lists
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.time.LocalDateTime
import kotlin.math.abs
import java.sql.Blob

class UserProfileFragment : Fragment(), View.OnTouchListener {

    //    appbar
    private lateinit var backButton: ImageView
    private lateinit var settingButton: ImageView
    private lateinit var shareButton: ImageView

    //    nested scrollbar- main body
    private lateinit var mainBody: NestedScrollView
    private lateinit var loadingAnimation: LottieAnimationView

    //    user main profile
    private lateinit var userProfileImage: ShapeableImageView
    private lateinit var userName: TextView
    private lateinit var userOccupation: TextView
    private lateinit var userHeadline: TextView
    private lateinit var userProfileEditButton: ImageView
    private lateinit var sendChatRequest: TextView

    //  about
    private lateinit var aboutHeader: RelativeLayout
    private lateinit var addNewAbout: ImageView
    private lateinit var hiddenAboutLayout: LinearLayout
    private lateinit var aboutContent: TextView
    private lateinit var editAbout: ImageView

    //    work experience
    private lateinit var workExperienceHeader: RelativeLayout
    private lateinit var addNewWorkExperience: ImageView
    private lateinit var workExperienceRecyclerView: RecyclerView

    //    education
    private lateinit var educationHeader: RelativeLayout
    private lateinit var addNewEducation: ImageView
    private lateinit var educationRecyclerView: RecyclerView

    //    skill
    private lateinit var skillHeader: RelativeLayout
    private lateinit var addNewSkill: ImageView
    private lateinit var skillRecyclerView: RecyclerView
    private lateinit var seeMoreSkill: TextView
    private lateinit var skill_divider: View

    //    project
    private lateinit var projectHeader: RelativeLayout
    private lateinit var addNewProject: ImageView
    private lateinit var projectRecyclerView: RecyclerView

    //    certificate
    private lateinit var certificateHeader: RelativeLayout
    private lateinit var addNewCertificate: ImageView
    private lateinit var certificateRecyclerView: RecyclerView

    //    profile
    private lateinit var profileHeader: RelativeLayout
    private lateinit var addNewProfile: ImageView
    private lateinit var profileRecyclerView: RecyclerView

    //    appreciation
    private lateinit var appreciationHeader: RelativeLayout
    private lateinit var addNewAppreciation: ImageView
    private lateinit var appreciationRecyclerView: RecyclerView

    //    language
    private lateinit var languageHeader: RelativeLayout
    private lateinit var addNewLanguage: ImageView
    private lateinit var languageRecyclerView: RecyclerView
    private lateinit var seeMoreLanguage: TextView

    //    resume
    private lateinit var resumeHeader: RelativeLayout
    private lateinit var addNewResume: ImageView
    private lateinit var resumeRecyclerView: RecyclerView
    private lateinit var pdfFileName: TextView
    private lateinit var pdfFileSize: TextView
    private lateinit var pdfTimeStamp: TextView
    private lateinit var uploadCardView: CardView
    private lateinit var afterUploadCardView: CardView
    private lateinit var saveButton: MaterialButton
    private var pdfFileData: ByteArray? = null

    //    hobbies
    private lateinit var hobbiesHeader: RelativeLayout
    private lateinit var addNewHobbies: ImageView
    private lateinit var hobbiesRecyclerView: RecyclerView
    private lateinit var seeMoreHobbies: TextView

    //    list
    private var educationList: MutableList<Education> = mutableListOf()
    private var skillList: MutableList<String> = mutableListOf()
    private val projectList: MutableList<Project> = mutableListOf()
    private val experienceList: MutableList<Experience> = mutableListOf()
    private val certificateList: MutableList<Certificate> = mutableListOf()
    private val languageList: MutableList<String> = mutableListOf()
    private val profileList: MutableList<Profile> = mutableListOf()
    private val appreciationList: MutableList<Appreciation> = mutableListOf()
    private val hobbiesList: MutableList<String> = mutableListOf()
    private val resumeList: MutableList<Pdf> = mutableListOf()


    private var skills: MutableList<String> = mutableListOf()
    private var languages: MutableList<String> = mutableListOf()
    private var hobbies: MutableList<String> = mutableListOf()

    // Adapters
    private lateinit var educationAdapter: EducationAdapter

    private lateinit var skillAdapter: SkillAdapter
    private lateinit var languageAdapter: SkillAdapter
    private lateinit var hobbiesAdapter: SkillAdapter
    private lateinit var dynamicSpanSizeLookupSearchSkill: DynamicSpanSizeLookupSkill
    private lateinit var dynamicSpanSizeLookupSkill: DynamicSpanSizeLookupSkill
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var experienceAdapter: ExperienceAdapter
    private lateinit var certificateAdapter: CertificateAdapter
    private lateinit var resumeAdapter: ResumeAdapter

    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var appreciationAdapter: AppreciationAdapter

    private lateinit var sharedPref: SharedPreferences
    private lateinit var tokenManager: TokenManager

    private lateinit var userProfileDto: UserProfileDto
    private var searchedUser: UserDto? = null
    private lateinit var currentUser: UserDto
    private var isSameUser: Boolean = true
    private var isSkillFetched: Boolean = false
    private var isLanguageFetched: Boolean = false
    private var isHobbiesFetched: Boolean = false
    private var chattingInfo: ChattingUserInfo? = null

    private val PICK_IMAGE_REQUEST = 1
    private val STORAGE_PERMISSION_CODE = 101 // You can use any unique number
    private lateinit var userProfileEditImg: ShapeableImageView
    private var userProfileImageData: Bitmap? = null

    //    view-model
    private lateinit var userViewModel: UserViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var chatRoomViewModel: ChatRoomViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var experienceViewModel: ExperienceViewModel
    private lateinit var educationViewModel: EducationViewModel
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var certificateViewModel: CertificateViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var appreciationViewModel: AppreciationViewModel
    private lateinit var skillViewModel: SkillViewModel
    private lateinit var hobbiesViewModel: HobbiesViewModel
    private lateinit var languageViewModel: LanguageViewModel
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var resumeViewModel: PdfViewModel
    private lateinit var notificationViewModel: NotificationViewModel

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var matrix = Matrix()
    private var scaleFactor = 1.0f

    private var touchStartX = 0f
    private var touchStartY = 0f
    private lateinit var selectedImage: ShapeableImageView
    private var imageUri: Uri? = null

    private var currentUserProfileImage: Image? = null
    private var currentToast: Toast? = null
    private lateinit var notificationManager: NotificationManager

    //    network connection
    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet: Boolean = true

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(Bundle())
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)

        networkManager = NetworkManager(requireContext())           // initializing network manager
        checkForInternetConnection()

        customErrorSnackBar = CustomErrorSnackBar()

//        fetching from bundle
        searchedUser = if (arguments?.getSerializable("userInfo") != null) {
            arguments?.getSerializable("userInfo") as UserDto
        } else {
            null
        }
        val searchedUserId = arguments?.getLong("userId")


//        initializing the
        CommonFunction.SharedPrefsUtil.init(requireContext())
        sharedPref = requireActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        tokenManager = TokenManager(requireContext())

        notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        initializing current user info
        currentUser = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()!!


//      initializing view model's

        userViewModel =
            ViewModelProvider(this, UserViewModelFactory(tokenManager))[UserViewModel::class.java]
        userProfileViewModel = ViewModelProvider(
            this,
            UserProfileViewModelFactory(tokenManager, requireContext())
        )[UserProfileViewModel::class.java]

        chatRoomViewModel = ViewModelProvider(
            this,
            ChatRoomViewModelFactory(tokenManager)
        )[ChatRoomViewModel::class.java]


        experienceViewModel = ViewModelProvider(
            this,
            ExperienceViewModelFactory(tokenManager)
        )[ExperienceViewModel::class.java]

        educationViewModel = ViewModelProvider(
            this,
            EducationViewModelFactory(tokenManager)
        )[EducationViewModel::class.java]

        projectViewModel = ViewModelProvider(
            this,
            ProjectViewModelFactory(tokenManager)
        )[ProjectViewModel::class.java]

        certificateViewModel = ViewModelProvider(
            this,
            CertificateViewModelFactory(tokenManager)
        )[CertificateViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(tokenManager)
        )[ProfileViewModel::class.java]

        appreciationViewModel = ViewModelProvider(
            this,
            AppreciationViewModelFactory(tokenManager)
        )[AppreciationViewModel::class.java]

        skillViewModel =
            ViewModelProvider(this, SkillViewModelFactory(tokenManager))[SkillViewModel::class.java]

        languageViewModel = ViewModelProvider(
            this,
            LanguageViewModelFactory(tokenManager)
        )[LanguageViewModel::class.java]

        hobbiesViewModel = ViewModelProvider(
            this,
            HobbiesViewModelFactory(tokenManager)
        )[HobbiesViewModel::class.java]

        imageViewModel = ViewModelProvider(
            this,
            ImageViewModelFactory(tokenManager, requireContext())
        )[ImageViewModel::class.java]

        resumeViewModel =
            ViewModelProvider(this, PdfViewModelFactory(tokenManager))[PdfViewModel::class.java]

        notificationViewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(tokenManager)
        )[NotificationViewModel::class.java]

        chatViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory(tokenManager, requireContext())
        )[ChatViewModel::class.java]


        mainBody = rootView.findViewById(R.id.userProfile_fragment_body)
        loadingAnimation = rootView.findViewById(R.id.loading_animation)


//        initialization of appBar fields
        backButton = rootView.findViewById(R.id.user_profile_appbar_backBtn)
        settingButton = rootView.findViewById(R.id.profile_settingBtn)
        shareButton = rootView.findViewById(R.id.profile_shareBtn)

//        initialization of user-personal-info
        userProfileImage = rootView.findViewById(R.id.profile_user_profile_img)
        userName = rootView.findViewById(R.id.profile_user_name)
        userOccupation = rootView.findViewById(R.id.profile_user_currentOccupation)
        userHeadline = rootView.findViewById(R.id.profile_user_headline)
        userProfileEditButton = rootView.findViewById(R.id.profile_personal_info_edit)
        sendChatRequest = rootView.findViewById(R.id.profile_send_chat_request)

//        about field initialization
        aboutHeader = rootView.findViewById(R.id.profile_user_about_me_header)
        addNewAbout = rootView.findViewById(R.id.profile_user_about_me_add_more_icon)
        hiddenAboutLayout = rootView.findViewById(R.id.profile_user_about_me_content_container)
        aboutContent = rootView.findViewById(R.id.profile_user_about_me)

//      work experience initialization
        workExperienceHeader = rootView.findViewById(R.id.profile_user_experience_header)
        addNewWorkExperience = rootView.findViewById(R.id.profile_user_experience_add_more_icon)
        workExperienceRecyclerView =
            rootView.findViewById(R.id.profile_user_experience_recyclerView)

//      education initialization
        educationHeader = rootView.findViewById(R.id.profile_user_education_header)
        addNewEducation = rootView.findViewById(R.id.profile_user_education_add_more_icon)
        educationRecyclerView = rootView.findViewById(R.id.profile_user_education_recyclerView)

//        skill initialization
        skillHeader = rootView.findViewById(R.id.profile_user_skill_header)
        addNewSkill = rootView.findViewById(R.id.profile_user_skill_add_more_icon)
        skillRecyclerView = rootView.findViewById(R.id.profile_user_skill_recyclerView)
        seeMoreSkill = rootView.findViewById(R.id.profile_user_skill_see_more)
        skill_divider = rootView.findViewById(R.id.skill_breaker_line)

//        project initialization
        projectHeader = rootView.findViewById(R.id.profile_user_project_header)
        addNewProject = rootView.findViewById(R.id.profile_user_project_add_more_icon)
        projectRecyclerView = rootView.findViewById(R.id.profile_user_project_recyclerView)

//        certificate initialization
        certificateHeader = rootView.findViewById(R.id.profile_user_certificate_header)
        addNewCertificate = rootView.findViewById(R.id.profile_user_certificate_add_more_icon)
        certificateRecyclerView = rootView.findViewById(R.id.profile_user_certificate_recyclerView)

//        appreciation initialization
        appreciationHeader = rootView.findViewById(R.id.profile_user_appreciation_header)
        addNewAppreciation = rootView.findViewById(R.id.profile_user_appreciation_add_more_icon)
        appreciationRecyclerView =
            rootView.findViewById(R.id.profile_user_appreciation_recyclerView)

//        profile initialization
        profileHeader = rootView.findViewById(R.id.profile_user_profile_header)
        addNewProfile = rootView.findViewById(R.id.profile_user_profile_add_more_icon)
        profileRecyclerView = rootView.findViewById(R.id.profile_user_profile_recyclerView)

//        language initialization
        languageHeader = rootView.findViewById(R.id.profile_user_language_header)
        addNewLanguage = rootView.findViewById(R.id.profile_user_language_add_more_icon)
        languageRecyclerView = rootView.findViewById(R.id.profile_user_language_recyclerView)
        seeMoreLanguage = rootView.findViewById(R.id.profile_user_language_see_more)

//        resume initialization
        resumeHeader = rootView.findViewById(R.id.profile_user_resume_header)
        addNewResume = rootView.findViewById(R.id.profile_user_resume_add_more_icon)
        resumeRecyclerView = rootView.findViewById(R.id.profile_user_resume_recyclerView)

//        hobbies initialization
        hobbiesHeader = rootView.findViewById(R.id.profile_user_hobbies_header)
        addNewHobbies = rootView.findViewById(R.id.profile_user_hobbies_add_more_icon)
        hobbiesRecyclerView = rootView.findViewById(R.id.profile_user_hobbies_recyclerView)
        seeMoreHobbies = rootView.findViewById(R.id.profile_user_hobbies_see_more)

//      checking if userInfo and current user is same or not


        if (searchedUser == null && searchedUserId != null ) {

            userViewModel.getUserById(searchedUserId, object : UpdateUserCallback {
                override fun onUserUpdated(updatedUserDto: UserDto) {
                    loadingAnimation.visibility = View.GONE
                    mainBody.visibility = View.VISIBLE
                    searchedUser = updatedUserDto
                    getUserProfileDto()
                    updateUserInfo()
                    setUserInfo()
                }

                override fun onUpdateUserError() {
                    loadingAnimation.visibility = View.GONE
                    mainBody.visibility = View.VISIBLE
                    searchedUser = currentUser
                    updateUserInfo()
                    setUserInfo()
                    getUserProfileDto()
                }

            })


        } else {
            loadingAnimation.visibility = View.GONE
            mainBody.visibility = View.VISIBLE
            if (searchedUser == null) {
                searchedUser = currentUser
            }
            updateUserInfo()
            setUserInfo()
            getUserProfileDto()
        }
        chatViewModel.chatUserInfo.observe(viewLifecycleOwner) {

            chattingInfo = it
            if (it == null || it.roomId < 1) {
                sendChatRequest.text = "Send request"
            } else {
                sendChatRequest.text = "Message"
            }

        }

        sendChatRequest.setOnClickListener {
            if (chattingInfo == null || chattingInfo!!.roomId < 1) {
                sendRequest()

            } else {
                val intent = Intent(requireContext(), ChattingActivity::class.java)
                intent.putExtra("user_id", searchedUserId)
                intent.putExtra("chatRoomId", chattingInfo!!.roomId)
                startActivity(intent)

            }

        }

        settingButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }


//        fetching user-profile-dto-info from backend
        userProfile()

//        function of about me
        aboutMe()

//        function of work experience
        workExperience()

//        function of education
        education()

//        function call skill
        skill()

//        function call project
        project()

//        function call certificate
        certificate()

//        function call appreciation
        appreciation()

//        function call profile
        profile()

//      function call hobbies
        hobbies()

//        function call language
        language()

//        function call resume
        resume()

//        setting value's in user-personal-info
//        setUserInfo()

        imageViewModel.imageResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                val bitmap =
                    BitmapFactory.decodeByteArray(it.bytes(), 0, it.contentLength().toInt())
                userProfileImageData = bitmap
// Assuming imageView is the ImageView where you want to display the profile picture
                userProfileImage.setImageBitmap(bitmap)

            }
        }
//        calling backButton
        backButton.setOnClickListener {
            // Navigate back to the previous fragment/activity
            requireActivity().onBackPressed()
        }

        shareButton.setOnClickListener {
            userProfileShare()
        }

        return rootView
    }

    private fun sendRequest() {
        chatRoomViewModel.sendChatRequest(currentUser.id,searchedUser!!.id,Chat(0,0,currentUser.id,searchedUser!!.id,"Your profile looks great and fit for the software role, So if you are interested we can discuss further",LocalDateTime.now().toString(),false))
        chatRoomViewModel.isRequestSent.observe(viewLifecycleOwner) { isRequestSent ->
            if(isRequestSent){
                sendChatRequest.text="Pending"
                sendChatRequest.isClickable=false
                sendNotification(searchedUser!!)
            }
        }
        chatRoomViewModel.error.observe(viewLifecycleOwner){ error->
            if(error.isNullOrEmpty()){
                showCustomErrorSnackBar("Something went wrong")
            }else{
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun updateUserInfo() {

        if (searchedUser?.username != currentUser.username) {

            isSameUser = false

            settingButton.visibility = View.GONE
            userProfileEditButton.visibility = View.GONE
            addNewAbout.visibility = View.GONE
            addNewWorkExperience.visibility = View.GONE
            addNewEducation.visibility = View.GONE
            addNewProfile.visibility = View.GONE
            addNewCertificate.visibility = View.GONE
            addNewSkill.visibility = View.GONE
            addNewProject.visibility = View.GONE
            addNewHobbies.visibility = View.GONE
            addNewAppreciation.visibility = View.GONE
            addNewResume.visibility = View.GONE
            addNewLanguage.visibility = View.GONE
            shareButton.visibility = View.VISIBLE
            sendChatRequest.visibility = View.VISIBLE

        } else {
            sendChatRequest.visibility = View.GONE
            searchedUser = currentUser

        }
        getUserProfilePicture()

        if (!isSameUser) {
            chatViewModel.getUserInfo(currentUser.id, searchedUser!!.id)
            chatViewModel.getUserInfo(searchedUser!!.id, currentUser.id)

        }
    }

    private fun userProfileShare() {

        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link =
                Uri.parse("https://hirequest.page.link/user-profile?userId=${searchedUser!!.username}")
            domainUriPrefix = "https://hirequest.page.link"
            androidParameters { }
        }

        val dynamicLinkUri = dynamicLink.uri

// Share the dynamic link
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "https://app.goo.gl/?userId=${searchedUser!!.username}".toString()
            )
            type = "text/plain"
        }
        startActivityForResult(shareIntent, 101)

    }

    //    used to set name/headline/currentOccupation
    private fun setUserInfo() {
        userName.text = searchedUser!!.name
        userHeadline.text = searchedUser!!.headline
        userOccupation.text = searchedUser!!.currentOccupation
    }

    private fun getUserProfilePicture() {
        imageViewModel.getUserProfilePicture(searchedUser!!.id)
    }

    private fun userProfile() {
        userProfileEditButton.setOnClickListener {
            editUserProfilePopup()
        }
    }

    private fun editUserProfilePopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.user_profile_edit_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)


        val chooseImage: TextView = popupView.findViewById(R.id.user_profile_edit_change_img_btn)
        val fullName: TextInputEditText =
            popupView.findViewById(R.id.user_profile_edit_popup_fullName_edittext)
        val userName: TextInputEditText =
            popupView.findViewById(R.id.user_profile_edit_popup_username_edittext)
        val currentPosition: TextInputEditText =
            popupView.findViewById(R.id.user_profile_edit_popup_current_position_edittext)
        val topSkills: TextInputEditText =
            popupView.findViewById(R.id.user_profile_edit_popup_top_skills_edittext)
        val addNewCurrentPosition: TextView =
            popupView.findViewById(R.id.user_profile_edit_popup_add_current_position_title)
        val manageTopSkill: TextView =
            popupView.findViewById(R.id.user_profile_edit_popup_add_top_skills_title)
        val personalInfoButton: TextView =
            popupView.findViewById(R.id.user_profile_edit_popup_edit_personal_info_button)
        val saveButton: MaterialButton =
            popupView.findViewById(R.id.user_profile_edit_popup_save_button)



        fullName.setText(searchedUser!!.name)
        userName.setText(searchedUser!!.username)
        currentPosition.setText(searchedUser!!.currentOccupation)
        topSkills.setText(searchedUser!!.headline)


        saveButton.setOnClickListener {
            val user = UserDto(
                currentUser.id,
                currentUser.birthdate,
                currentPosition.text.toString(),
                currentUser.email,
                topSkills.text.toString(),
                fullName.text.toString(),
                currentUser.phone,
                currentUser.status,
                userName.text.toString(),
                currentUser.isRecuriter,
                currentUser.gender
            )

            if (userName.text.toString().isEmpty() || fullName.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill the required fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                userViewModel.updateUserDetails(user, currentUser.id, object : UpdateUserCallback {
                    override fun onUserUpdated(updatedUserDto: UserDto) {
                        CommonFunction.SharedPrefsUtil.updateUserResponseFromSharedPreferences(
                            updatedUserDto
                        )
                        currentUser = updatedUserDto
                        popupWindow.dismiss()
                    }

                    override fun onUpdateUserError() {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
        }



        personalInfoButton.setOnClickListener {
            openPersonalInfoEditFun()
        }


        userProfileEditImg = popupView.findViewById(R.id.user_profile_edit_profile_img)
        userProfileEditImg.setImageBitmap(userProfileImageData)

        chooseImage.setOnClickListener {
            if (!isStoragePermissionGranted()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                imageEditPage()

//                uploadCardView.visibility=View.GONE
//                afterUploadCardView.visibility=View.VISIBLE
            }
        }


        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.READ_EXTERNAL_STORAGE"
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request the permission if it is not granted
    private fun requestStoragePermission() {
        if (!isStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission is already granted, proceed with the operation
            openImagePicker()
        }
    }

    // Handle the result of the permission request
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the operation
                openImagePicker()
            } else {
                // Permission denied, handle it
                // You may want to show a message or disable functionality that requires this permission
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    imageUri = uri
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

//                    val imageData :ByteArray = Base64.decode(bytes, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)

// Assuming imageView is the ImageView where you want to display the profile picture
                    selectedImage.setImageBitmap(bitmap)
                    selectedImage.setOnTouchListener(this)
                    scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())


                    // Step 1: Get the Drawable from the ShapeableImageView
                    val drawable: Drawable? = selectedImage.drawable

                    if (drawable is BitmapDrawable) {
                        // Step 2: Get the Bounds of the Visible Area
                        val visibleBounds: Rect = selectedImage.drawable.bounds

                        // Step 3: Create a Bitmap of the Visible Area
                        val originalBitmap: Bitmap = drawable.bitmap
                        val visibleBitmap: Bitmap = Bitmap.createBitmap(
                            originalBitmap,
                            visibleBounds.left,
                            visibleBounds.top,
                            visibleBounds.width(),
                            visibleBounds.height()
                        )
                        userProfileEditImg.setImageBitmap(visibleBitmap)
                        // Now 'visibleBitmap' contains only the visible part of the original image
                        // You can use 'visibleBitmap' as needed
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun imageEditPage() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.profile_image_edit_layout, null)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show()

//        val inflater = LayoutInflater.from(requireContext())
//        val popupView = inflater.inflate(R.layout.profile_image_edit_layout, null)
//        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        // Set background drawable
//        popupWindow.animationStyle= R.style.PopupAnimation
//// Set outside touch-ability
//        popupWindow.isOutsideTouchable = true
//// Set focusability
//        popupWindow.isFocusable = true
//        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        selectedImage = bottomSheetDialog.findViewById(R.id.profile_picture_edit)!!
        selectedImage.setImageBitmap(userProfileImageData)

        val changeImage: Button? = bottomSheetDialog.findViewById(R.id.change_image)
        val uploadImage: Button? = bottomSheetDialog.findViewById(R.id.upload_image)

        changeImage!!.setOnClickListener {
            openImagePicker()
        }

        if (imageUri != null) {

            val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            val imageData: ByteArray = Base64.decode(bytes, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            selectedImage.setImageBitmap(bitmap)
        }


        uploadImage!!.setOnClickListener {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {

                val requestFile = bytes
                    .toRequestBody(
                        "application/img".toMediaTypeOrNull(),
                        0, getFileSize(imageUri!!).toInt()
                    )

                val filePart = MultipartBody.Part.createFormData("file", "", requestFile)
                imageViewModel.addProfileImage(currentUser.id, filePart)
            }
            bottomSheetDialog.dismiss()
        }
        changeImage.setOnClickListener {
            openImagePicker()
        }
    }

    private val getPdfContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle PDF selection here
            if (uri != null) {
                try {
                    val name = getFileName(uri)
                    val fileType = getFileType(uri)
                    val fileSize = getFileSize(uri)
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    val timeStamp = LocalDateTime.now()
                    var hour = timeStamp.hour
                    val min = timeStamp.minute
                    val day = timeStamp.dayOfMonth
                    val year = timeStamp.year
                    val month = timeStamp.month


                    val time = if (hour >= 12) {
                        hour %= 12
                        "${hour}:${min} pm"

                    } else {
                        "${hour}:${min} am"
                    }
//${CommonFunction(requireContext()).getMonth("$month-$year")}
                    val monthYear = "$day ${month.toString().substring(0, 3).toLowerCase()} $year"


                    // Update the UI with selected PDF details
                    uploadCardView.visibility = View.GONE
                    afterUploadCardView.visibility = View.VISIBLE
                    saveButton.visibility = View.VISIBLE
                    pdfFileName.text = name
                    pdfFileSize.text = fileSize.toString() + " kb"
                    pdfTimeStamp.text = "$monthYear $time"

                    pdfFileData = bytes


                    // Add any additional logic specific to PDF selection

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun openImagePicker() {
        getContent.launch("image/*")
    }


    private fun hobbies() {
        hobbiesHeader.setOnClickListener {
            if (hobbiesRecyclerView.visibility == View.VISIBLE) {
                hobbiesRecyclerView.visibility = View.GONE
                seeMoreHobbies.visibility = View.GONE
            } else {
                hobbiesRecyclerView.visibility = View.VISIBLE
                fetchingHobbiesInfo()
            }
        }

        addNewHobbies.setOnClickListener {

            fetchingHobbiesInfo()

            showHobbiesPopup()
        }
    }

    private fun fetchingHobbiesInfo() {
        if (!::hobbiesAdapter.isInitialized) {
            hobbiesAdapter = SkillAdapter(hobbiesList, 6)
            hobbiesRecyclerView.adapter = hobbiesAdapter
            hobbiesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        viewLifecycleOwner.lifecycleScope.launch {

            if (!isHobbiesFetched) {
                hobbiesViewModel.getAllHobbies(userProfileDto.id.toLong())
            }
        }
        hobbiesViewModel.hobbiesList.observe(viewLifecycleOwner) { hobbies ->
            if (!hobbies.isNullOrEmpty()) {
                hobbiesList.clear()
                hobbiesList.addAll(hobbies)
                hobbiesAdapter.update(hobbies)
            }
        }
        hobbiesViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showCustomErrorSnackBar(error)
            } else {
                showCustomErrorSnackBar("Something went wrong")
            }
        }
        hobbiesViewModel.loading.observe(viewLifecycleOwner) { isLoading ->

        }
    }


    private fun resume() {

        resumeHeader.setOnClickListener {
            if (resumeRecyclerView.visibility == View.VISIBLE) {
                resumeRecyclerView.visibility = View.GONE
            } else {
                resumeRecyclerView.visibility = View.VISIBLE
                if (!::resumeAdapter.isInitialized) {
                    resumeAdapter = ResumeAdapter(resumeList, isSameUser)
                    resumeRecyclerView.adapter = resumeAdapter
                    resumeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    resumeViewModel.getAllPdf(userProfileDto.id.toLong())
                }
                resumeViewModel.pdfList.observe(viewLifecycleOwner) { pdfs ->
                    resumeList.clear()
                    if (pdfs != null) {
                        resumeList.addAll(pdfs)
                    }
                    resumeAdapter.updateData(resumeList)
                }

                resumeAdapter.setOnEditIconClickListener(object :
                    ResumeAdapter.OnEditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        if (resumeList[position].id > 0) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                resumeViewModel.deletePdf(resumeList[position].id)
                                resumeList.removeAt(position)
                                resumeAdapter.notifyItemRemoved(position)
                            }

                        } else {
                            someThingWentWrong()
                        }
                    }
                })

                resumeAdapter.setOnDownloadIconClickListener(object :
                    ResumeAdapter.OnDownloadIconClickListener {
                    override fun onDownloadIconClick(position: Int) {

                        viewLifecycleOwner.lifecycleScope.launch {
                            resumeViewModel.downloadPdf(resumeList[position].id)
//                                resumeAdapter.notifyDataSetChanged()
//                                val body=it
//                                val inputStream: InputStream = body.byteStream()
//                                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${currentUser.username}-hireQuest.pdf")
//                                val outputStream: OutputStream = FileOutputStream(file)
//                                val buffer = ByteArray(4096)
//                                var bytesRead: Int
//
//                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                                    outputStream.write(buffer, 0, bytesRead)
//                                }
//
//                                outputStream.flush()
//                                outputStream.close()
//                                inputStream.close()
//                                currentToast?.cancel() // Cancel the previous toast if it exists
//                                currentToast = Toast.makeText(requireContext(),"Downloaded",Toast.LENGTH_SHORT)
//                                currentToast?.show()
//
//                                showDownloadNotification()
//
//                            }

                        }
                    }

                })
            }
        }
        addNewResume.setOnClickListener {
            showResumePopup()
        }
    }

    private fun createNotificationChannel() {

        // Check if the channel already exists (required for Android Oreo and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Download Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDownloadNotification() {
        // Create the notification channel (call this only once)
        createNotificationChannel()

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Unique notification ID
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(requireContext(), "download_channel")
            .setSmallIcon(R.drawable.icon_pdf)
            .setContentTitle("Pdf downloaded")
            .setContentText("${currentUser.username} - hireQuest")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        // Use a unique notification ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun language() {

        languageHeader.setOnClickListener {
            if (languageRecyclerView.visibility == View.VISIBLE) {
                languageRecyclerView.visibility = View.GONE
                seeMoreLanguage.visibility = View.GONE
            } else {
                languageRecyclerView.visibility = View.VISIBLE

                if (::languageAdapter.isInitialized) {
                    languageAdapter.update(languageList, 5)
                    if (languageList.size > 5) {
                        seeMoreLanguage.visibility = View.VISIBLE
                    }

                }
                fetchingLanguageInfo()

            }
        }

        addNewLanguage.setOnClickListener {

            if (!::languageAdapter.isInitialized) {
                languageAdapter = SkillAdapter(languageList, 5)
                languageRecyclerView.adapter = languageAdapter
                languageRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
            fetchingLanguageInfo()
            showLanguagePopup()
        }

        seeMoreLanguage.setOnClickListener {
            languageAdapter.update(languageList, Int.MAX_VALUE)
            seeMoreLanguage.visibility = View.GONE
        }
    }

    private fun fetchingLanguageInfo() {
        if (!::languageAdapter.isInitialized) {
            languageAdapter = SkillAdapter(languageList, 5)
            languageRecyclerView.adapter = languageAdapter
            languageRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            if (!isLanguageFetched) {
                languageViewModel.getAllLanguage(userProfileDto.id.toLong())
            }
        }
        languageViewModel.languageList.observe(viewLifecycleOwner) { languages ->
            if (!languages.isNullOrEmpty()) {
                languageList.clear()
                languageList.addAll(languages)
                languageAdapter.update(languageList, 5)
                isLanguageFetched = true
                if (languageList.size > 5) {
                    seeMoreLanguage.visibility = View.VISIBLE
                }
            }
        }
        languageViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showCustomErrorSnackBar(error)
            } else {
                showCustomErrorSnackBar("Something went wrong")
            }
        }

    }

    private fun profile() {

        profileHeader.setOnClickListener {
            if (profileRecyclerView.visibility == View.VISIBLE) {
                profileRecyclerView.visibility = View.GONE
            } else {
                profileRecyclerView.visibility = View.VISIBLE

                fetchingProfileLinkInfo()

                profileAdapter.setOnEditIconClickListener(object :
                    ProfileAdapter.OnEditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        showProfilePopup(position)
                    }

                })
            }
        }
        addNewProfile.setOnClickListener {
            fetchingProfileLinkInfo()
            showProfilePopup(-1)
        }
    }

    private fun fetchingProfileLinkInfo() {
        if (!::profileAdapter.isInitialized) {
            profileAdapter = ProfileAdapter(profileList, isSameUser)
            profileRecyclerView.adapter = profileAdapter
            profileRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.getAllProfileLink(userProfileDto.id.toLong())
        }
        profileViewModel.profileLinkList.observe(viewLifecycleOwner) { profileLinks ->
            profileList.clear()
            if (profileLinks != null) {
                profileList.addAll(profileLinks)
            }
            profileAdapter.updateData(profileList)
        }

        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showCustomErrorSnackBar(error)
            } else {
                showCustomErrorSnackBar("Something went wrong")
            }
        }


    }

    private fun appreciation() {

        appreciationHeader.setOnClickListener {
            if (appreciationRecyclerView.visibility == View.VISIBLE) {
                appreciationRecyclerView.visibility = View.GONE
            } else {
                appreciationRecyclerView.visibility = View.VISIBLE
                fetchingAppreciationInfo()

                appreciationAdapter.setOnEditIconClickListener(object :
                    AppreciationAdapter.OnEditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        showAppreciationPopup(position)
                    }

                })
            }
        }
        addNewAppreciation.setOnClickListener {
            showAppreciationPopup(-1)
        }
    }

    private fun fetchingAppreciationInfo() {
        if (!::appreciationAdapter.isInitialized) {
            appreciationAdapter = AppreciationAdapter(appreciationList, isSameUser)
            appreciationRecyclerView.adapter = appreciationAdapter
            appreciationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            appreciationViewModel.getAllAppreciation(userProfileDto.id.toLong())
        }
        appreciationViewModel.appreciationList.observe(viewLifecycleOwner) { appreciations ->
            appreciationList.clear()
            if (appreciations != null) {
                appreciationList.addAll(appreciations)
            }

            appreciationAdapter.updateData(appreciationList)
        }
        appreciationViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun certificate() {

        certificateHeader.setOnClickListener {
            if (certificateRecyclerView.visibility == View.VISIBLE) {
                certificateRecyclerView.visibility = View.GONE
            } else {
                certificateRecyclerView.visibility = View.VISIBLE
                fetchingCertificateInfo()
                certificateAdapter.setOnEditIconClickListener(object :
                    CertificateAdapter.OnEditIconClickListener {
                    override fun onEditIconClick(position: Int) {
                        showCertificatePopup(position)
                    }
                })
            }
        }

        addNewCertificate.setOnClickListener {
            fetchingCertificateInfo()
            showCertificatePopup(-1)

        }
    }

    private fun fetchingCertificateInfo() {
        if (!::certificateAdapter.isInitialized) {
            certificateAdapter = CertificateAdapter(certificateList, isSameUser)
            certificateRecyclerView.adapter = certificateAdapter
            certificateRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            certificateViewModel.getAllCertificate(userProfileDto.id.toLong())
        }
        certificateViewModel.certificateList.observe(viewLifecycleOwner) { certificates ->
            certificateList.clear()
            if (certificates != null) {
                certificateList.addAll(certificates)
            }
            certificateAdapter.updateData(certificateList)
        }
        certificateViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun project() {

        projectHeader.setOnClickListener {
            if (projectRecyclerView.visibility == View.VISIBLE) {
                projectRecyclerView.visibility = View.GONE
            } else {
                projectRecyclerView.visibility = View.VISIBLE

                fetchingProjectInfo()
                projectAdapter.setOnEditIconClickListener(
                    object : ProjectAdapter.OnEditIconClickListener {
                        override fun onEditIconClick(position: Int) {
                            showProjectPopup(position)
                        }
                    }
                )
            }
        }

        addNewProject.setOnClickListener {
            fetchingProjectInfo()
            showProjectPopup(-1)
        }
    }

    private fun fetchingProjectInfo() {
        if (!::projectAdapter.isInitialized) {
            projectAdapter = ProjectAdapter(projectList, isSameUser)
            projectRecyclerView.adapter = projectAdapter
            projectRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            projectViewModel.getAllProject(userProfileDto.id.toLong())
        }
        projectViewModel.projectList.observe(viewLifecycleOwner) { projects ->
            projectList.clear()
            if (projects != null) {
                projectList.addAll(projects)
            }
            projectAdapter.updateData(projectList)
        }
        projectViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun skill() {

        skillHeader.setOnClickListener {
            if (skillRecyclerView.visibility == View.VISIBLE) {
                skillRecyclerView.visibility = View.GONE
                seeMoreSkill.visibility = View.GONE
                skill_divider.visibility = View.GONE
            } else {
                skillRecyclerView.visibility = View.VISIBLE
                skill_divider.visibility = View.VISIBLE
                if (skillList.size > 6) {
                    seeMoreSkill.visibility = View.VISIBLE
                }
                if (::skillAdapter.isInitialized) {
                    skillAdapter.update(skillList, 6)
                }
            }
        }
        addNewSkill.setOnClickListener {
            fetchingSkillInfo()
            showSkillPopup(skillList)
        }

        seeMoreSkill.setOnClickListener {
            skillAdapter.update(skillList, Int.MAX_VALUE)
            seeMoreSkill.visibility = View.GONE
        }
    }

    private fun fetchingSkillInfo() {
        if (!::skillAdapter.isInitialized) {
            skillAdapter = SkillAdapter(skillList, 6)
            skillRecyclerView.adapter = skillAdapter
            val skillLayoutManager = GridLayoutManager(requireContext(), 3)
            dynamicSpanSizeLookupSkill = DynamicSpanSizeLookupSkill(skillList)
            skillLayoutManager.spanSizeLookup = dynamicSpanSizeLookupSkill
            skillRecyclerView.layoutManager = skillLayoutManager
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (!isSkillFetched) {
                skillViewModel.getSkills(userProfileDto.id.toLong())
            }
        }
        skillViewModel.skillList.observe(viewLifecycleOwner) { skills ->
            if (!skills.isNullOrEmpty()) {
                skillList.clear()
                skillList.addAll(skills)
                skillAdapter.update(skillList, 6)
                isSkillFetched = true
                if (skillList.size > 6) {
                    seeMoreSkill.visibility = View.VISIBLE
                }
            }
        }
        skillViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun education() {

        educationHeader.setOnClickListener {
            if (educationRecyclerView.visibility == View.VISIBLE) {
                educationRecyclerView.visibility = View.GONE
            } else {
                educationRecyclerView.visibility = View.VISIBLE

                if (!::educationAdapter.isInitialized) {
                    educationAdapter = EducationAdapter(educationList, isSameUser)
                    educationRecyclerView.adapter = educationAdapter
                    educationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
                fetchingEducationInfo()

                educationAdapter.setOnEditIconClickListener(
                    object : EducationAdapter.OnEditIconClickListener {
                        override fun onEditIconClick(position: Int) {
                            showEducationPopup(position)
                        }

                    }
                )
            }
        }
        addNewEducation.setOnClickListener {
            fetchingEducationInfo()
            showEducationPopup(-1)
        }
    }

    private fun fetchingEducationInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            educationViewModel.getAllEducation(userProfileDto.id.toLong())
        }
        educationViewModel.educationList.observe(viewLifecycleOwner) { educations ->
            educationList.clear()
            if (educations != null) {
                educationList.addAll(educations)
            }
            educationAdapter.updateData(educationList)
        }
        educationViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun workExperience() {

        workExperienceHeader.setOnClickListener {

            if (workExperienceRecyclerView.visibility == View.VISIBLE) {
                workExperienceRecyclerView.visibility = View.GONE

            } else {
                workExperienceRecyclerView.visibility = View.VISIBLE
                fetchingExperienceInfo()

                experienceAdapter.setOnEditIconClickListener(
                    object : ExperienceAdapter.OnEditIconClickListener {
                        override fun onEditIconClick(position: Int) {
                            showWorkExperiencePopup(position)
                        }

                    }
                )
            }
        }

        addNewWorkExperience.setOnClickListener {
            fetchingExperienceInfo()
            showWorkExperiencePopup(-1)
        }
    }

    private fun fetchingExperienceInfo() {
        if (!::experienceAdapter.isInitialized) {
            experienceAdapter = ExperienceAdapter(experienceList, isSameUser)
            workExperienceRecyclerView.adapter = experienceAdapter
            workExperienceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            experienceViewModel.getAllExperience(userProfileDto.id.toLong())
        }
        experienceViewModel.experienceList.observe(viewLifecycleOwner) { experiences ->
            experienceList.clear()
            if (experiences != null) {
                experienceList.addAll(experiences)
                experienceAdapter.updateData(experienceList)
            }
        }
        experienceViewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                showCustomErrorSnackBar(error)
            }
        }

    }

    private fun aboutMe() {


        addNewAbout.setOnClickListener {

            showAboutPopup(userProfileDto.about)
        }

        aboutHeader.setOnClickListener {
            if (hiddenAboutLayout.visibility == View.GONE) {
                hiddenAboutLayout.visibility = View.VISIBLE
                addNewAbout.setImageResource(R.drawable.icon_edit)
                if (userProfileDto.about != null && userProfileDto.about.isNotEmpty()) {
                    aboutContent.visibility = View.VISIBLE
                    aboutContent.text = userProfileDto.about
                } else {
                    aboutContent.visibility = View.GONE
                }

                addNewAbout.setOnClickListener {
                    showAboutPopup(aboutContent.text.toString())
                }

            } else {
                hiddenAboutLayout.visibility = View.GONE
            }
        }
    }


//    private fun setUserInfo() {
//        userName.text=userInfo.name
//        userHeadline.text=userInfo.headline
//        userOccupation.text=userInfo.currentOccupation
//        setProfilePicture(userInfo.image.data)
//    }

    private fun setProfilePicture(imageView: ImageView) {
        if (currentUserProfileImage != null) {
            val imageData: ByteArray = Base64.decode(currentUserProfileImage!!.data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

// Assuming imageView is the ImageView where you want to display the profile picture
            imageView.setImageBitmap(bitmap)
        }

    }


    private fun showProfilePopup(position: Int) {


        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.profile_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val popupTitle: TextView = popupView.findViewById(R.id.profile_popup_title)
        val handleName: TextInputEditText =
            popupView.findViewById(R.id.profile_popup_handle_name_editText)
        val profileLink: TextInputEditText =
            popupView.findViewById(R.id.profile_popup_profile_link_editText)
        val description: TextInputEditText =
            popupView.findViewById(R.id.profile_popup_profile_description_editText)
        val saveButton: MaterialButton = popupView.findViewById(R.id.profile_popup_save_button)
        val removeButton: MaterialButton = popupView.findViewById(R.id.profile_popup_remove_button)

        if (position >= 0) {
            popupTitle.text = "Update profile"
            val profile = profileList[position]
            handleName.setText(profile.handleName)
            profileLink.setText(profile.profileUrl)
            description.setText(profile.description)

            removeButton.visibility = View.VISIBLE
        }

        saveButton.setOnClickListener {
            if (handleName.text.toString().isNotEmpty() && profileLink.text.toString()
                    .isNotEmpty()
            ) {

                val profile = Profile(
                    0,
                    handleName.text.toString(),
                    profileLink.text.toString(),
                    description.text.toString()
                )


                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        profileViewModel.updateProfileLink(
                            profile,
                            profileList[position].id.toLong()
                        )
                    }
                    profileViewModel.updatedProfileLink.observe(viewLifecycleOwner) { updatedProfile ->
                        profileList.removeAt(position)
                        profileList.add(position, updatedProfile)
                        profileAdapter.notifyItemInserted(position)
                        popupWindow.dismiss()
                        currentToast?.cancel() // Cancel the previous toast if it exists
                        currentToast =
                            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT)
                        currentToast?.show()
                    }
                    profileViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }

                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        profileViewModel.addProfileLink(profile, userProfileDto.id.toLong())
                    }
                    profileViewModel.createdProfileLink.observe(viewLifecycleOwner) { profile ->
                        if (!::profileAdapter.isInitialized) {
                            profileAdapter = ProfileAdapter(profileList, isSameUser)
                            profileRecyclerView.adapter = profileAdapter
                            profileRecyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                        }
                        profileList.add(profile)
                        profileAdapter.notifyItemInserted(profileList.size - 1)
                        popupWindow.dismiss()
                        currentToast?.cancel() // Cancel the previous toast if it exists
                        currentToast =
                            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT)
                        currentToast?.show()

                    }
                    profileViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                }

            } else {
                someThingWentWrong()
            }
        }

        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            val heading: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            heading.text = "Remove profile"

            bottomSheetDialog.show()
            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    profileViewModel.deleteProfileLink(profileList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${profileList[position].handleName} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                profileList.removeAt(position)
                profileAdapter.updateData(profileList)
                profileAdapter.notifyItemRemoved(position)


            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun showWorkExperiencePopup(position: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.work_experience_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val popupTitle: TextView = popupView.findViewById(R.id.work_experience_popup_title)
        val jobTitle: TextInputEditText =
            popupView.findViewById(R.id.work_experience_popup_job_title_edittext)
        val company: TextInputEditText =
            popupView.findViewById(R.id.work_experience_popup_company_edittext)
        val startDate: TextInputEditText =
            popupView.findViewById(R.id.work_experience_popup_start_date)
        val endDate: TextInputEditText = popupView.findViewById(R.id.work_experience_popup_end_date)
        val descriptionEditText: TextInputEditText =
            popupView.findViewById(R.id.work_experience_popup_description_edittext)
        val submitButton: MaterialButton =
            popupView.findViewById(R.id.work_experience_popup_save_button)
        val removeButton: MaterialButton =
            popupView.findViewById(R.id.work_experience_popup_remove_button)

        if (position >= 0) {
            popupTitle.text = "Update work experience"
            val experience = experienceList[position]
            jobTitle.setText(experience.title)
            company.setText(experience.organisation)
            startDate.setText(experience.start)
            endDate.setText(experience.end)
            descriptionEditText.setText(experience.description)
        }
        startDate.setOnClickListener {
            showDateSelectionWindow("start") { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDateSelectionWindow("end") { selectedDate ->
                endDate.setText(selectedDate)
            }

        }


        submitButton.setOnClickListener {
            val experience = Experience(
                0,
                jobTitle.text.toString(),
                company.text.toString(),
                startDate.text.toString(),
                endDate.text.toString(),
                descriptionEditText.text.toString()
            )

            if (jobTitle.text.toString().isNotEmpty() && company.text.toString()
                    .isNotEmpty() && startDate.text.toString()
                    .isNotEmpty() && endDate.text.toString().isNotEmpty()
            ) {

                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        experienceViewModel.updateExperience(
                            experience,
                            experienceList[position].id.toLong()
                        )
                    }

                    experienceViewModel.updatedExperience.observe(viewLifecycleOwner) { updatedExperience ->
                        experienceList.removeAt(position)
                        experienceList.add(position, updatedExperience)
                        experienceAdapter.notifyItemChanged(position)
                        popupWindow.dismiss()

                        currentToast?.cancel() // Cancel the previous toast if it exists
                        currentToast =
                            Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT)
                        currentToast?.show()
                    }
                    experienceViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }

                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        experienceViewModel.addExperience(
                            experience,
                            userProfileDto.id.toLong()
                        )
                    }
                    experienceViewModel.createdExperience.observe(viewLifecycleOwner) { experience ->
                        experienceList.add(experience)
                        experienceAdapter.notifyItemInserted(experienceList.size - 1)
                        popupWindow.dismiss()
                    }
                    experienceViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill all required field",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            bottomSheetDialog.show()
            val title: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            title.text = "Remove work experience"
            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    experienceViewModel.deleteExperience(experienceList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${experienceList[position].title} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                experienceList.removeAt(position)
                experienceAdapter.updateData(experienceList)
                experienceAdapter.notifyItemRemoved(position)
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun showEducationPopup(position: Int) {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.education_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView =
            popupView.findViewById(R.id.back_button)
        val popupTitle: TextView =
            popupView.findViewById(R.id.education_popup_title)
        val degree: TextInputEditText =
            popupView.findViewById(R.id.education_popup_degree_title_edittext)
        val institute: TextInputEditText =
            popupView.findViewById(R.id.education_popup_institute_edittext)
        val fieldOfStudy: TextInputEditText =
            popupView.findViewById(R.id.education_popup_fieldOfStudy_edittext)
        val startDate: TextInputEditText =
            popupView.findViewById(R.id.education_popup_start_date)
        val endDate: TextInputEditText = popupView.findViewById(R.id.education_popup_end_date)
        val saveButton: MaterialButton = popupView.findViewById(R.id.education_popup_save_button)
        val removeButton: MaterialButton =
            popupView.findViewById(R.id.education_popup_remove_button)

        if (position >= 0) {
            popupTitle.text = "Update education"
            val education = educationList[position]
            degree.setText(education.levelOfEducation)
            institute.setText(education.instituteName)
            fieldOfStudy.setText(education.fieldOfStudy)
            startDate.setText(education.start)
            endDate.setText(education.end)

        }

        startDate.setOnClickListener {
            showDateSelectionWindow("start") { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDateSelectionWindow("end") { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        saveButton.setOnClickListener {
            if (degree.text.toString().isNotEmpty() && institute.text.toString()
                    .isNotEmpty() && fieldOfStudy.text.toString()
                    .isNotEmpty() && startDate.text.toString()
                    .isNotEmpty() && endDate.text.toString().isNotEmpty()
            ) {
                val education = Education(
                    0,
                    endDate.text.toString(),
                    institute.text.toString(),
                    degree.text.toString(),
                    startDate.text.toString(),
                    fieldOfStudy.text.toString()
                )

                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        educationViewModel.updateEducation(
                            education,
                            educationList[position].id.toLong()
                        )
                    }
                    educationViewModel.updatedEducation.observe(viewLifecycleOwner) { updatedEducation ->
                        educationList.removeAt(position)
                        educationList.add(position, updatedEducation)
                        educationAdapter.notifyItemChanged(position)
                        popupWindow.dismiss()
                    }
                    educationViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }

                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        educationViewModel.addEducation(education, userProfileDto.id.toLong())
                    }
                    educationViewModel.createdEducation.observe(viewLifecycleOwner) { education ->
                        if (!::educationAdapter.isInitialized) {
                            educationAdapter = EducationAdapter(educationList, isSameUser)
                            educationRecyclerView.adapter = educationAdapter
                            educationRecyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                        }
                        educationList.add(education)
                        educationAdapter.notifyItemInserted(educationList.size - 1)
                        popupWindow.dismiss()
                    }
                    educationViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                }
            } else {

                Toast.makeText(requireContext(), "All Fields are required!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            bottomSheetDialog.show()
            val title: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            title.text = "Remove education"
            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    educationViewModel.deleteEducation(educationList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${educationList[position].instituteName} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                educationList.removeAt(position)
                educationAdapter.updateData(educationList)
                educationAdapter.notifyItemRemoved(position)
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }


    }

    private fun showSkillPopup(list: MutableList<String>) {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.skill_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields
        skills.clear()
        skills.addAll(skillList)
        val skillSearchResultsAdapter = SearchSkillAdapter(skills)

        val showAllSkillAdapter = CommonAdapter(skills)

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val searchRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_search_recyclerview)
        val skillRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_skill_recyclerview)
        val searchEditText: TextInputEditText =
            popupView.findViewById(R.id.skill_popup_search_edittext)
        val saveButton: MaterialButton = popupView.findViewById(R.id.skill_popup_save_button)

        skillRecyclerView.visibility = View.VISIBLE

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val layoutManager = GridLayoutManager(requireContext(), 3)
        skillRecyclerView.layoutManager = layoutManager
        dynamicSpanSizeLookupSearchSkill = DynamicSpanSizeLookupSkill(skills)
        layoutManager.spanSizeLookup = dynamicSpanSizeLookupSearchSkill


        val predefinedSkills = Lists().allSkills
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredSkills =
                        predefinedSkills.filter { it.contains(query, ignoreCase = true) }
                    if (filteredSkills.isEmpty()) {
                        searchRecyclerView.visibility = View.GONE
                        skillRecyclerView.visibility = View.VISIBLE
                    } else {
                        searchRecyclerView.visibility = View.VISIBLE
                        skillRecyclerView.visibility = View.GONE
                        skillSearchResultsAdapter.updateData(filteredSkills)
                    }
                } else {
                    skillRecyclerView.visibility = View.VISIBLE
                    searchRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        skillSearchResultsAdapter.onSkillItemClickListener =
            object : SearchSkillAdapter.OnSkillItemClickListener {
                override fun onSkillItemClick(skill: String) {
                    skills.add(skill)
                    searchEditText.setText("")
                    showAllSkillAdapter.updateData(skills)
                    showAllSkillAdapter.notifyDataSetChanged()
                }
            }

        searchRecyclerView.adapter = skillSearchResultsAdapter
        skillRecyclerView.adapter = showAllSkillAdapter

        saveButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                skillViewModel.addSkill(skills, userProfileDto.id.toLong())
            }
            skillViewModel.addedSkills.observe(viewLifecycleOwner) { addedSkills ->
                if (!::skillAdapter.isInitialized) {
                    skillAdapter = SkillAdapter(skillList, 6)
                    skillRecyclerView.adapter = skillAdapter
                    val skillLayoutManager = GridLayoutManager(requireContext(), 3)
                    dynamicSpanSizeLookupSkill = DynamicSpanSizeLookupSkill(addedSkills)
                    skillLayoutManager.spanSizeLookup = dynamicSpanSizeLookupSkill
                    skillRecyclerView.layoutManager = skillLayoutManager
                }
                skillList.clear()
                skillList.addAll(addedSkills)
                skillAdapter.update(skillList, 6)
                if (skillList.size > 6) {
                    seeMoreSkill.visibility = View.VISIBLE
                }
                popupWindow.dismiss()
            }
            skillViewModel.error.observe(viewLifecycleOwner) { error ->
                if (error.isNullOrEmpty()) {
                    showCustomErrorSnackBar("Something went wrong")
                } else {
                    showCustomErrorSnackBar(error)
                }
            }
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun showHobbiesPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.skill_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields
        hobbies.clear()
        hobbies.addAll(hobbiesList)
        val hobbiesSearchResultsAdapter = SearchSkillAdapter(hobbies)

        val showAllHobbiesAdapter = CommonAdapter(hobbies)

        val backButton: ImageView =
            popupView.findViewById(R.id.back_button)
        val popupTitle: TextView =
            popupView.findViewById(R.id.skill_popup_title)
        val searchRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_search_recyclerview)
        val hobbiesRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_skill_recyclerview)
        val searchEditText: TextInputEditText =
            popupView.findViewById(R.id.skill_popup_search_edittext)
        val saveButton: MaterialButton = popupView.findViewById(R.id.skill_popup_save_button)

        popupTitle.text = "Add Hobbies"
        searchEditText.hint = "Search hobbies"
        hobbiesRecyclerView.visibility = View.VISIBLE

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val layoutManager = GridLayoutManager(requireContext(), 3)
        hobbiesRecyclerView.layoutManager = layoutManager


        val predefinedSkills = Lists().allSkills
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredSkills =
                        predefinedSkills.filter { it.contains(query, ignoreCase = true) }
                    if (filteredSkills.isEmpty()) {
                        searchRecyclerView.visibility = View.GONE
                        hobbiesRecyclerView.visibility = View.VISIBLE
                    } else {
                        searchRecyclerView.visibility = View.VISIBLE
                        hobbiesRecyclerView.visibility = View.GONE
                        hobbiesSearchResultsAdapter.updateData(filteredSkills)
                    }
                } else {
                    hobbiesRecyclerView.visibility = View.VISIBLE
                    searchRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        hobbiesSearchResultsAdapter.onSkillItemClickListener =
            object : SearchSkillAdapter.OnSkillItemClickListener {
                override fun onSkillItemClick(skill: String) {
                    hobbies.add(skill)
                    searchEditText.setText("")
                    showAllHobbiesAdapter.updateData(hobbies)
                    showAllHobbiesAdapter.notifyDataSetChanged()
                }
            }

        searchRecyclerView.adapter = hobbiesSearchResultsAdapter
        hobbiesRecyclerView.adapter = showAllHobbiesAdapter

        saveButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                hobbiesViewModel.addHobbies(hobbies, userProfileDto.id.toLong())
            }
            hobbiesViewModel.addedHobbies.observe(viewLifecycleOwner) { addedHobbies ->
                if (!::hobbiesAdapter.isInitialized) {
                    hobbiesAdapter = SkillAdapter(hobbiesList, 6)
                    hobbiesRecyclerView.adapter = hobbiesAdapter
                    hobbiesRecyclerView.layoutManager =
                        GridLayoutManager(requireContext(), 3)
                }
                hobbiesList.clear()
                hobbiesList.addAll(addedHobbies)
                hobbiesAdapter.update(hobbiesList, 6)
                if (hobbiesList.size > 6) {
                    seeMoreHobbies.visibility = View.VISIBLE
                }
                popupWindow.dismiss()
            }

            hobbiesViewModel.error.observe(viewLifecycleOwner) { error ->
                if (error.isNullOrEmpty()) {
                    showCustomErrorSnackBar("Something went wrong")
                } else {
                    showCustomErrorSnackBar(error)
                }
            }
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun showLanguagePopup() {


        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.skill_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields
        languages.clear()
        languages.addAll(languageList)
        val languageSearchResultsAdapter = SearchSkillAdapter(languages)

        val showAllLanguageAdapter = CommonAdapter(languages)

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val searchRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_search_recyclerview)
        val languageRecyclerView: RecyclerView =
            popupView.findViewById(R.id.skill_popup_skill_recyclerview)
        val searchEditText: TextInputEditText =
            popupView.findViewById(R.id.skill_popup_search_edittext)
        val saveButton: MaterialButton = popupView.findViewById(R.id.skill_popup_save_button)

        languageRecyclerView.visibility = View.VISIBLE

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val layoutManager = GridLayoutManager(requireContext(), 3)
        languageRecyclerView.layoutManager = layoutManager


        val predefinedLanguages = Lists().uniqueLanguages
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val filteredLanguages =
                        predefinedLanguages.filter { it.contains(query, ignoreCase = true) }
                    if (filteredLanguages.isEmpty()) {
                        searchRecyclerView.visibility = View.GONE
                        languageRecyclerView.visibility = View.VISIBLE
                    } else {
                        searchRecyclerView.visibility = View.VISIBLE
                        languageRecyclerView.visibility = View.GONE
                        languageSearchResultsAdapter.updateData(filteredLanguages)
                    }
                } else {
                    languageRecyclerView.visibility = View.VISIBLE
                    searchRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        languageSearchResultsAdapter.onSkillItemClickListener =
            object : SearchSkillAdapter.OnSkillItemClickListener {
                override fun onSkillItemClick(skill: String) {
                    languages.add(skill)
                    searchEditText.setText("")
                    showAllLanguageAdapter.updateData(languages)
                    showAllLanguageAdapter.notifyDataSetChanged()
                }
            }

        searchRecyclerView.adapter = languageSearchResultsAdapter
        languageRecyclerView.adapter = showAllLanguageAdapter

        saveButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                languageViewModel.addLanguage(languages, userProfileDto.id.toLong())
            }
            languageViewModel.addedLanguages.observe(viewLifecycleOwner) { addedLanguages ->
                if (!::skillAdapter.isInitialized) {
                    languageAdapter = SkillAdapter(languageList, 6)
                    languageRecyclerView.adapter = languageAdapter
                    languageRecyclerView.layoutManager =
                        GridLayoutManager(requireContext(), 3)
                }
                languageList.clear()
                languageList.addAll(addedLanguages)
                languageAdapter.update(languageList, 6)
                if (languageList.size > 6) {
                    seeMoreLanguage.visibility = View.VISIBLE
                }
                popupWindow.dismiss()
            }
        }
        languageViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                showCustomErrorSnackBar("Something went wrong")
            } else {
                showCustomErrorSnackBar(error)
            }
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun showDateSelectionWindow(type: String, callback: (String) -> Unit) {

        var selectedMonth = ""
        var selectedYear = ""
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.start_date_picker_popup, null)
        bottomSheetDialog.setContentView(view)

        // Customize and handle date selection logic here if needed

        val viewPager: ViewPager2? = bottomSheetDialog.findViewById(R.id.view_pager_month)
        val title: TextView? = bottomSheetDialog.findViewById(R.id.start_date_title)
        val saveBtn: MaterialButton? =
            bottomSheetDialog.findViewById(R.id.start_date_picker_selectDate_Button)
        val cancelBtn: MaterialButton? =
            bottomSheetDialog.findViewById(R.id.start_date_picker_cancel_Button)

        if (type == "start") {
            title?.text = "Start Date"
        } else {
            title?.text = "End Date"
        }


        //            getting current date
        val currentDate = LocalDateTime.now()
        val currMonth = currentDate.month.value

        val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        if (viewPager != null) {
            viewPager.clipToPadding = false
            viewPager.clipChildren = false
            viewPager.offscreenPageLimit = 1
            viewPager.setPadding(140, 0, 140, 0) // Adjust padding as needed

            val adapter = DateAdapter(requireContext(), months)
            viewPager.adapter = adapter

            viewPager.setCurrentItem(currMonth - 1, true)
            adapter.setSelectedItem(currMonth - 1)
            viewPager.setPageTransformer { page, position ->
                val absPosition = abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.setSelectedItem(position)

                }
            })
        } else {
            Log.d("viewpager", "sorry it is null")
        }


        val years = mutableListOf<String>()
        for (year in 1990..2050) {
            years.add(year.toString())
        }

        val yearAdapter = DateAdapter(requireContext(), years)
        val yearViewPager: ViewPager2? = bottomSheetDialog.findViewById(R.id.view_pager_year)
        if (yearViewPager != null) {
            yearViewPager.clipToPadding = false
            yearViewPager.clipChildren = false
            yearViewPager.offscreenPageLimit = 1
            yearViewPager.setPadding(140, 0, 140, 0) // Adjust padding as needed


            val currYear = currentDate.year
            yearViewPager.adapter = yearAdapter
            yearViewPager.setCurrentItem(currYear, true)
            yearAdapter.setSelectedItem(currYear)
            yearViewPager.setPageTransformer { page, position ->
                val absPosition = Math.abs(position)
                page.scaleY = 1 - (absPosition * 0.3f) // Scale the page
            }

            yearViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    yearAdapter.setSelectedItem(position)


                }
            })
        } else {
            Log.d("viewpager", "sorry it is null")
        }

        bottomSheetDialog.show()

        saveBtn?.setOnClickListener {
            // This code will be executed when the user clicks the save button
            // It will update selectedMonth and selectedYear with the current selected values
            selectedMonth = months[viewPager?.currentItem ?: 0]
            selectedYear = years[yearViewPager?.currentItem ?: 0]

            val selectedDate = "$selectedMonth $selectedYear"
            callback(selectedDate) // Call the callback with the selected date
            bottomSheetDialog.dismiss()
            bottomSheetDialog.dismiss()
        }
    }

    private fun showAppreciationPopup(position: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.appreciation_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val popupTitle: TextView = popupView.findViewById(R.id.appreciation_popup_title)
        val appreciationTitle: TextInputEditText =
            popupView.findViewById(R.id.appreciation_popup_appreciation_title_edittext)
        val issuedBy: TextInputEditText =
            popupView.findViewById(R.id.appreciation_popup_issuedBy_edittext)
        val appreciationLink: TextInputEditText =
            popupView.findViewById(R.id.appreciation_popup_appreciation_link_edittext)
        val startDate: TextInputEditText =
            popupView.findViewById(R.id.appreciation_popup_start_date)
        val endDate: TextInputEditText = popupView.findViewById(R.id.appreciation_popup_end_date)
        val descriptionEditText: TextInputEditText =
            popupView.findViewById(R.id.appreciation_popup_description_edittext)

        val saveButton: MaterialButton = popupView.findViewById(R.id.appreciation_popup_save_button)
        val removeButton: MaterialButton =
            popupView.findViewById(R.id.appreciation_popup_remove_button)

        if (position >= 0) {
            popupTitle.text = "Update appreciation"
            val appreciation = appreciationList[position]
            appreciationTitle.setText(appreciation.appreciationTitle)
            appreciationLink.setText(appreciation.appreciationUrl)
            issuedBy.setText(appreciation.issuedBy)
            startDate.setText(appreciation.start)
            endDate.setText(appreciation.end)
            descriptionEditText.setText(appreciation.description)
        }

        startDate.setOnClickListener {
            showDateSelectionWindow("start") { selectedDate ->
                startDate.setText(selectedDate)
            }

        }

        endDate.setOnClickListener {
            showDateSelectionWindow("end") { selectedDate ->
                endDate.setText(selectedDate)
            }

        }

        saveButton.setOnClickListener {
            if (appreciationTitle.text.toString().isNotEmpty() && appreciationLink.text.toString()
                    .isNotEmpty() && issuedBy.text.toString()
                    .isNotEmpty() && startDate.text.toString()
                    .isNotEmpty() && endDate.text.toString().isNotEmpty()
            ) {

                val appreciation = Appreciation(
                    0,
                    appreciationTitle.text.toString(),
                    appreciationLink.text.toString(),
                    issuedBy.text.toString(),
                    endDate.text.toString(),
                    startDate.text.toString(),
                    descriptionEditText.text.toString()
                )

                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        appreciationViewModel.updateAppreciation(
                            appreciation,
                            appreciationList[position].id.toLong()
                        )
                    }
                    appreciationViewModel.updatedAppreciation.observe(viewLifecycleOwner) { updatedAppreciation ->
                        if (updatedAppreciation != null) {
                            appreciationList.removeAt(position)
                            appreciationList.add(position, updatedAppreciation)
                            appreciationAdapter.notifyItemChanged(position)
                            popupWindow.dismiss()
                        }
                    }
                    appreciationViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        appreciationViewModel.addAppreciation(
                            appreciation,
                            userProfileDto.id.toLong()
                        )
                    }
                    appreciationViewModel.createdAppreciation.observe(viewLifecycleOwner) { addedAppreciation ->
                        if (!::appreciationAdapter.isInitialized) {
                            appreciationAdapter =
                                AppreciationAdapter(appreciationList, isSameUser)
                            appreciationRecyclerView.adapter = appreciationAdapter
                            appreciationRecyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                        }
                        if (addedAppreciation != null) {
                            appreciationList.add(addedAppreciation)
                            appreciationAdapter.notifyItemInserted(appreciationList.size - 1)
                            popupWindow.dismiss()
                        }
                    }
                    appreciationViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Fill all required fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            val heading: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            heading.text = "Remove Appreciation"


            bottomSheetDialog.show()

            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    appreciationViewModel.deleteAppreciation(appreciationList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${appreciationList[position].appreciationTitle} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                appreciationList.removeAt(position)
                appreciationAdapter.updateData(appreciationList)
                appreciationAdapter.notifyItemRemoved(position)
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun showProjectPopup(position: Int) {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.project_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)

        val popupTitle: TextView = popupView.findViewById(R.id.project_popup_title)
        val projectTitle: TextInputEditText =
            popupView.findViewById(R.id.project_popup_project_title_edittext)
        val projectUrl: TextInputEditText =
            popupView.findViewById(R.id.project_popup_project_url_edittext)
        val startDate: TextInputEditText = popupView.findViewById(R.id.project_popup_start_date)
        val endDate: TextInputEditText = popupView.findViewById(R.id.project_popup_end_date)
        val descriptionEditText: TextInputEditText =
            popupView.findViewById(R.id.project_popup_description_edittext)

        val saveButton: MaterialButton = popupView.findViewById(R.id.project_popup_save_button)
        val removeButton: MaterialButton = popupView.findViewById(R.id.project_popup_remove_button)
//

        if (position >= 0) {
            popupTitle.text = "Update project"
            removeButton.visibility = View.VISIBLE

            val project = projectList[position]
            projectTitle.setText(project.title)
            projectUrl.setText(project.projectUrl)
            startDate.setText(project.start)
            endDate.setText(project.end)
            descriptionEditText.setText(project.description)

        }

        startDate.setOnClickListener {
            showDateSelectionWindow("start") { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDateSelectionWindow("end") { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        saveButton.setOnClickListener {

            if (projectTitle.text.toString().isNotEmpty() && projectUrl.text.toString()
                    .isNotEmpty() && startDate.text.toString()
                    .isNotEmpty() && endDate.text.toString().isNotEmpty()
            ) {

                val project = Project(
                    0,
                    descriptionEditText.text.toString(),
                    projectUrl.text.toString(),
                    projectTitle.text.toString(),
                    startDate.text.toString(),
                    endDate.text.toString()
                )

                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        projectViewModel.updateProject(
                            project,
                            projectList[position].id.toLong()
                        )
                    }
                    projectViewModel.updatedProject.observe(viewLifecycleOwner) { updatedProject ->
                        if (updatedProject != null) {
                            projectList.removeAt(position)
                            projectList.add(position, updatedProject)
                            profileAdapter.notifyItemChanged(position)
                            popupWindow.dismiss()
                        }
                    }
                    projectViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        projectViewModel.addProject(project, userProfileDto.id.toLong())
                        projectViewModel.createdProject.observe(viewLifecycleOwner) { addedProject ->
                            if (!::projectAdapter.isInitialized) {
                                projectAdapter = ProjectAdapter(projectList, isSameUser)
                                projectRecyclerView.adapter = projectAdapter
                                profileRecyclerView.layoutManager =
                                    LinearLayoutManager(requireContext())

                            }
                            if (addedProject != null) {
                                projectList.add(project)
                                projectAdapter.notifyItemInserted(projectList.size - 1)
                                popupWindow.dismiss()

                            }
                        }
                        projectViewModel.error.observe(viewLifecycleOwner) { error ->
                            if (error.isNullOrEmpty()) {
                                showCustomErrorSnackBar("Something went wrong")
                            } else {
                                showCustomErrorSnackBar(error)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Fill all required fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            bottomSheetDialog.show()
            val title: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            title.text = "Remove project"
            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    projectViewModel.deleteProject(projectList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${projectList[position].title} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                projectList.removeAt(position)
                projectAdapter.updateData(projectList)
                projectAdapter.notifyItemRemoved(position)
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }


    }

    private fun showCertificatePopup(position: Int) {

        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.certificate_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


//            initializing all fields

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        val popupTitle: TextView = popupView.findViewById(R.id.certificate_popup_title)
        val certificateTitle: TextInputEditText =
            popupView.findViewById(R.id.certificate_popup_certificate_title_edittext)
        val certificateUrl: TextInputEditText =
            popupView.findViewById(R.id.certificate_popup_certificate_link_edittext)
        val issuedBy: TextInputEditText =
            popupView.findViewById(R.id.certificate_popup_issuedBy_edittext)
        val startDate: TextInputEditText =
            popupView.findViewById(R.id.certificate_popup_start_date)
        val endDate: TextInputEditText = popupView.findViewById(R.id.certificate_popup_end_date)
        val description: TextInputEditText =
            popupView.findViewById(R.id.certificate_popup_description_edittext)

        val saveButton: MaterialButton = popupView.findViewById(R.id.certificate_popup_save_button)
        val removeButton: MaterialButton =
            popupView.findViewById(R.id.certificate_popup_remove_button)


        if (position >= 0) {
            val certificate = certificateList[position]
            certificateTitle.setText(certificate.certificateTitle)
            certificateUrl.setText(certificate.certificateUrl)
            issuedBy.setText(certificate.issuedBy)
            startDate.setText(certificate.start)
            endDate.setText(certificate.end)
            description.setText(certificate.description)
        }

        startDate.setOnClickListener {
            showDateSelectionWindow("start") { selectedDate ->
                startDate.setText(selectedDate)
            }
        }

        endDate.setOnClickListener {
            showDateSelectionWindow("end") { selectedDate ->
                endDate.setText(selectedDate)
            }
        }

        saveButton.setOnClickListener {

            if (certificateTitle.text.toString().isNotEmpty() && certificateUrl.text.toString()
                    .isNotEmpty() && issuedBy.text.toString()
                    .isNotEmpty() && startDate.text.toString()
                    .isNotEmpty() && endDate.text.toString().isNotEmpty()
            ) {

                val certificate = Certificate(
                    0,
                    certificateTitle.text.toString(),
                    issuedBy.text.toString(),
                    startDate.text.toString(),
                    endDate.text.toString(),
                    certificateUrl.text.toString(),
                    description.text.toString()
                )

                if (position >= 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        certificateViewModel.updateCertificate(
                            certificate,
                            certificateList[position].id.toLong()
                        )
                    }
                    certificateViewModel.updatedCertificate.observe(viewLifecycleOwner) { updatedCertificate ->
                        if (updatedCertificate != null) {
                            certificateList.removeAt(position)
                            certificateList.add(position, updatedCertificate)
                            certificateAdapter.notifyItemChanged(position)
                            popupWindow.dismiss()
                        }
                    }
                    certificateViewModel.error.observe(viewLifecycleOwner) { error ->
                        if (error.isNullOrEmpty()) {
                            showCustomErrorSnackBar("Something went wrong")
                        } else {
                            showCustomErrorSnackBar(error)
                        }
                    }

                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        certificateViewModel.addCertificate(certificate, userProfileDto.id.toLong())
                        certificateViewModel.createdCertificate.observe(viewLifecycleOwner) { addedCertificate ->
                            if (!::certificateAdapter.isInitialized) {
                                certificateAdapter =
                                    CertificateAdapter(certificateList, isSameUser)
                                certificateRecyclerView.adapter = certificateAdapter
                                certificateRecyclerView.layoutManager =
                                    LinearLayoutManager(requireContext())
                            }
                            if (addedCertificate != null) {
                                certificateList.add(certificate)
                                certificateAdapter.notifyItemInserted(certificateList.size - 1)
                                popupWindow.dismiss()
                            }

                        }
                        certificateViewModel.error.observe(viewLifecycleOwner) { error ->
                            if (error.isNullOrEmpty()) {
                                showCustomErrorSnackBar("Something went wrong")
                            } else {
                                showCustomErrorSnackBar(error)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill required fields!", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        removeButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.custom_remove_undo_bottomsheet_layout, null)
            bottomSheetDialog.setContentView(view)

            bottomSheetDialog.show()
            val title: TextView = view.findViewById(R.id.custom_remove_undo_bottomSheet_title)
            val confirmButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_confirmation_Button)
            val cancelButton: MaterialButton =
                view.findViewById(R.id.custom_remove_undo_bottomSheet_cancel_Button)

            title.text = "Remove certificate"
            confirmButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    certificateViewModel.deleteCertificate(certificateList[position].id.toLong())
                }
                bottomSheetDialog.dismiss()
                popupWindow.dismiss()
                Toast.makeText(
                    requireContext(),
                    "${certificateList[position].certificateTitle} deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                certificateList.removeAt(position)
                certificateAdapter.updateData(certificateList)
                certificateAdapter.notifyItemRemoved(position)
            }
            cancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun getUserProfileDto() {
        userProfileViewModel.getCandidateDto(searchedUser!!.id, object : CandidateCallback {
            override fun onCandidateAdded(userProfileDto: UserProfileDto) {
                if (!userProfileDto.about.isNullOrEmpty()) {
                    addNewAbout.setImageResource(R.drawable.icon_edit)
                }
                this@UserProfileFragment.userProfileDto = userProfileDto
            }

            override fun onCandidateError() {

            }

        })
    }

    private fun showAboutPopup(text: String) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.about_me_edit_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation
// Set outside touch-ability
        popupWindow.isOutsideTouchable = true
// Set focusability
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val backButton: ImageView = popupView.findViewById(R.id.backButton)
        val aboutTextInputLayout: TextInputLayout =
            popupView.findViewById(R.id.about_me_popup_text_input_layout)
        val aboutEditText: TextInputEditText = popupView.findViewById(R.id.about_me_popup_editText)
        val saveButton: MaterialButton = popupView.findViewById(R.id.about_me_popup_submit_button)
        val submitButtonLoadingAnimation: LottieAnimationView =
            popupView.findViewById(R.id.submit_button_loading_animation)
        if (aboutEditText.text.toString().isNotEmpty()) {
            aboutTextInputLayout.helperText = ""
        }
        aboutEditText.setOnClickListener {
            aboutTextInputLayout.helperText = ""
        }
        aboutEditText.setText(text)
        saveButton.setOnClickListener {

            submitButtonLoadingAnimation.visibility = View.VISIBLE
            val userProfile = UserProfileDto(aboutEditText.text.toString(), userProfileDto.id)
            if (aboutEditText.text.toString().isNotEmpty()) {
                userProfileViewModel.updateUserProfile(
                    userProfileDto.id.toLong(),
                    userProfile,
                    object : CandidateCallback {
                        override fun onCandidateAdded(userProfileDto: UserProfileDto) {
                            submitButtonLoadingAnimation.visibility = View.GONE
                            this@UserProfileFragment.userProfileDto = userProfileDto
                            aboutContent.text = userProfileDto.about
                            popupWindow.dismiss()

                        }

                        override fun onCandidateError() {
                            submitButtonLoadingAnimation.visibility = View.GONE
                            Toast.makeText(requireContext(), "Try again!", Toast.LENGTH_SHORT)
                                .show()
                            popupWindow.dismiss()
                        }

                    })
            } else {
                Toast.makeText(requireContext(), "It can't be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun showResumePopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.resume_popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

// Set background drawable
        popupWindow.animationStyle = R.style.PopupAnimation

// Set outside touch-ability
        popupWindow.isOutsideTouchable = true

// Set focusability
        popupWindow.isFocusable = true

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

// Initializing all fields
// ...

        val backButton: ImageView = popupView.findViewById(R.id.back_button)
        uploadCardView = popupView.findViewById(R.id.resume_popup_before_upload_container)
        afterUploadCardView = popupView.findViewById(R.id.resume_popup_after_upload_container)
        saveButton = popupView.findViewById(R.id.resume_popup_save_button)

        pdfFileName = popupView.findViewById(R.id.resume_popup_after_file_name)
        pdfFileSize = popupView.findViewById(R.id.resume_popup_after_file_size)
        pdfTimeStamp = popupView.findViewById(R.id.resume_popup_after_file_uploadedAt)
        val deleteButton =
            popupView.findViewById<ImageView>(R.id.resume_popup_after_delete_pdf_icon)
        val removeTextView =
            popupView.findViewById<TextView>(R.id.resume_popup_after_file_delete_text)
        uploadCardView.visibility = View.VISIBLE
        afterUploadCardView.visibility = View.GONE


        uploadCardView.setOnClickListener {
            if (!isStoragePermissionGranted()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                getPdfContent.launch("application/pdf")

//                uploadCardView.visibility=View.GONE
//                afterUploadCardView.visibility=View.VISIBLE
            }
        }

        saveButton.setOnClickListener {
            if (pdfFileData != null) {


                val requestFile = pdfFileData!!
                    .toRequestBody(
                        "application/pdf".toMediaTypeOrNull(),
                        0, pdfFileData!!.size
                    )

                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    pdfFileName.text.toString(),
                    requestFile
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    resumeViewModel.addPdf(userProfileDto.id.toLong(), filePart)
                }
                resumeViewModel.createdPdf.observe(viewLifecycleOwner) { addedPdf ->
                    if (!::resumeAdapter.isInitialized) {
                        resumeAdapter = ResumeAdapter(resumeList, isSameUser)
                        resumeRecyclerView.adapter = resumeAdapter
                        resumeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    }
                    if (addedPdf != null) {
                        resumeList.add(addedPdf)
                        resumeAdapter.notifyItemInserted(resumeList.size - 1)
                        Toast.makeText(requireContext(), "Uploaded!", Toast.LENGTH_SHORT).show()
                    }
                    popupWindow.dismiss()
                }
                resumeViewModel.error.observe(viewLifecycleOwner) { error ->
                    if (error.isNullOrEmpty()) {
                        showCustomErrorSnackBar("Something went wrong")
                    } else {
                        showCustomErrorSnackBar(error)
                    }
                }

            } else {
                Toast.makeText(requireContext(), "Please select valid file!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        deleteButton.setOnClickListener {
            pdfFileData = null
            afterUploadCardView.visibility = View.GONE
            uploadCardView.visibility = View.VISIBLE
            saveButton.visibility = View.INVISIBLE

        }
        removeTextView.setOnClickListener {
            pdfFileData = null
            afterUploadCardView.visibility = View.GONE
            uploadCardView.visibility = View.VISIBLE
            saveButton.visibility = View.INVISIBLE
        }
        backButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }

    private fun openPersonalInfoEditFun() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.personal_info_edit_popup_bottomsheet, null)
        bottomSheetDialog.setContentView(view)
// Set the height to match parent layout's height
        val parentLayout = view.parent as View
        val layoutParams = parentLayout.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        parentLayout.layoutParams = layoutParams

        val saveButton: ImageView? = bottomSheetDialog.findViewById(R.id.personal_info_saveButton)
        val email: TextInputEditText? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_email_edittext)
        val phone: TextInputEditText? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_phone_number_edittext)
        val countryCode: TextView? = bottomSheetDialog.findViewById(R.id.code_edittext)
        val spinner: ImageView? = bottomSheetDialog.findViewById(R.id.spinner_icon)
        val dob: TextInputEditText? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_dob_edittext)
        val maleRadioButton: MaterialRadioButton? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_gender_male)
        val femaleRadioButton: MaterialRadioButton? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_gender_female)
        val otherRadioButton: MaterialRadioButton? =
            bottomSheetDialog.findViewById(R.id.personal_info_edit_popup_gender_other)

        email?.setText(currentUser.email)
        dob?.setText(currentUser.birthdate)
        if (currentUser.phone != null) {
            phone?.setText(currentUser.phone!!.split(" ")[1])
            countryCode?.text = currentUser.phone!!.split(" ")[0]
        }
        var gender: String? = ""
        gender = currentUser.gender
        if (gender.equals("male")) {
            maleRadioButton?.isChecked = true
            femaleRadioButton?.isChecked = false
            otherRadioButton?.isChecked = false
        } else if (gender.equals("female")) {
            maleRadioButton?.isChecked = false
            femaleRadioButton?.isChecked = true
            otherRadioButton?.isChecked = false
        } else {
            maleRadioButton?.isChecked = false
            femaleRadioButton?.isChecked = false
            otherRadioButton?.isChecked = true
        }

// Set an OnCheckedChangeListener for each radio button


        maleRadioButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Male radio button is checked, uncheck the other two
                gender = "male"
                femaleRadioButton?.isChecked = false
                otherRadioButton?.isChecked = false
            }
        }

        femaleRadioButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Female radio button is checked, uncheck the other two
                gender = "female"
                maleRadioButton?.isChecked = false
                otherRadioButton?.isChecked = false
            }
        }

        otherRadioButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Other radio button is checked, uncheck the other two
                gender = "other"
                maleRadioButton?.isChecked = false
                femaleRadioButton?.isChecked = false
            }
        }


        val countryDialingCodes = listOf(
            "+1",   // United States
            "+86",  // China
            "+91",  // India
            "+62",  // Indonesia
            "+92",  // Pakistan
            "+55",  // Brazil
            "+234", // Nigeria
            "+880", // Bangladesh
            "+7",   // Russia
            "+52",  // Mexico
            "+81",  // Japan
            "+251", // Ethiopia
            "+63",  // Philippines
            "+20",  // Egypt
            "+84",  // Vietnam
            "+243", // Democratic Republic of the Congo
            "+90",  // Turkey
            "+98",  // Iran
            "+49",  // Germany
            "+66",  // Thailand
            "+44",  // United Kingdom
            "+33",  // France
            "+39",  // Italy
            "+27",  // South Africa
            "+95",  // Myanmar
            "+82",  // South Korea
            "+34",  // Spain
            "+380", // Ukraine
            "+255", // Tanzania
            "+54"   // Argentina
        )

        spinner?.setOnClickListener {
            val listPopupWindow = ListPopupWindow(requireContext())
            listPopupWindow.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    countryDialingCodes
                )
            )
            listPopupWindow.anchorView = spinner
            listPopupWindow.width = resources.getDimensionPixelSize(R.dimen.dropdown_width)
            listPopupWindow.height = resources.getDimensionPixelSize(R.dimen.dropdown_height)
            listPopupWindow.isModal = true
            listPopupWindow.setOnItemClickListener { _, _, position, _ ->
                countryCode?.text =
                    countryDialingCodes[position]
                listPopupWindow.dismiss()
            }
            listPopupWindow.show()
        }

        saveButton?.setOnClickListener {
            val userDto = UserDto(
                currentUser.id,
                dob?.text.toString(),
                currentUser.currentOccupation,
                email?.text.toString(),
                currentUser.headline,
                currentUser.name,
                countryCode?.text.toString() + " " + phone?.text.toString(),
                currentUser.status,
                currentUser.username,
                currentUser.isRecuriter,
                gender
            )

            if (email?.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill the required fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                userViewModel.updateUserDetails(
                    userDto,
                    currentUser.id,
                    object : UpdateUserCallback {
                        override fun onUserUpdated(updatedUserDto: UserDto) {
                            CommonFunction.SharedPrefsUtil.updateUserResponseFromSharedPreferences(
                                updatedUserDto
                            )
                            currentUser = updatedUserDto
                            bottomSheetDialog.dismiss()
                        }

                        override fun onUpdateUserError() {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }

        }


        bottomSheetDialog.show()
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = nameIndex?.let { cursor.getString(it) }
        cursor?.close()
        return name
    }

    private fun getFileType(uri: Uri): String? {
        return requireContext().contentResolver.getType(uri)
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
        cursor?.moveToFirst()
        var size = sizeIndex?.let { cursor.getLong(it) }
        cursor?.close()

        return size ?: 0
    }

    fun setBlobImage(blob: Blob) {
        val inputStream: InputStream = blob.binaryStream
        val bytes: ByteArray = inputStream.readBytes()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Assuming 'userProfileEditImg' is your ImageView
        userProfileEditImg.setImageBitmap(bitmap)
        userProfileImage.setImageBitmap(bitmap)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            matrix.postScale(
                detector.scaleFactor,
                detector.scaleFactor,
                detector.focusX,
                detector.focusY
            )

            // Calculate the new translation to keep the image centered
            val scaledWidth = selectedImage.width * detector.scaleFactor
            val scaledHeight = selectedImage.height * detector.scaleFactor
            val deltaX = (selectedImage.width - scaledWidth) / 2
            val deltaY = (selectedImage.height - scaledHeight) / 2

            matrix.postTranslate(deltaX, deltaY)
            selectedImage.imageMatrix = matrix

            return true
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        scaleGestureDetector?.onTouchEvent(event)

        val DRAG_SCALE_FACTOR = 0.01f // Adjust this value to control dragging speed

// Inside onTouch method

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                matrix.set(matrix)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - touchStartX) * DRAG_SCALE_FACTOR
                val dy = (event.y - touchStartY) * DRAG_SCALE_FACTOR

                matrix.set(matrix)
                matrix.postTranslate(dx, dy)
                selectedImage.imageMatrix = matrix
            }
        }

        return true
    }

    private fun someThingWentWrong() {
        currentToast?.cancel() // Cancel the previous toast if it exists
        currentToast = Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun checkForInternetConnection() {
        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    isConnectedToInternet = it.isConnected
                    if (!isConnectedToInternet) {
                        showCustomErrorSnackBar("No internet connection")
                    }
                }
        }
    }

    private fun showCustomErrorSnackBar(message: String) {
        customErrorSnackBar.showSnackbar(
            requireContext(),
            rootView,
            message
        )
    }

    private fun sendNotification(user: UserDto) {
        val request = NotificationRequest(
            currentUser.username,
            "Chat Request",
            "Sent chat request",
            user.username,
            Lists().notificationPref[1]
        )
        if (currentUser.username != user.username) {
            viewLifecycleOwner.lifecycleScope.launch {
                notificationViewModel.sendNotification(request)
            }
        }
    }

}