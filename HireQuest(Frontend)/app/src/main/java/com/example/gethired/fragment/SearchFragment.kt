package com.example.gethired.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.gethired.CommonFunction
import com.example.gethired.NetworkManagement.NetworkManager
import com.example.gethired.ProfileActivity
import com.example.gethired.R
import com.example.gethired.Token.TokenManager
import com.example.gethired.ViewModel.*
import com.example.gethired.adapter.FilterAdapter
import com.example.gethired.adapter.FilterItemAdapter
import com.example.gethired.adapter.RecentSearchAdapter
import com.example.gethired.adapter.UserProfileAdapter
import com.example.gethired.entities.*
import com.example.gethired.adapter.dynamicSpan.DynamicSpanSizeLookup
import com.example.gethired.factory.*
import com.example.gethired.snackbar.CustomErrorSnackBar
import com.example.gethired.utils.Lists
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SEARCH_STATE_KEY = "searchState"
const val CURRENT_FRAGMENT_KEY = 1

class SearchFragment : Fragment() {

    private lateinit var searchView: TextInputEditText
    private lateinit var recentSearchRecyclerView: RecyclerView
    private lateinit var userProfileRecyclerView: RecyclerView
    private lateinit var recentSearchContainer: LinearLayout
    private lateinit var recentSearchClearAll: TextView
    private lateinit var searchLoadingAnimation: LottieAnimationView

    //    filter
    private lateinit var filterContainer: LinearLayout
    private lateinit var filterButton: ImageView
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterAdapter: FilterAdapter

    private lateinit var filterItemAdapter: FilterItemAdapter
    private lateinit var dynamicSpanSizeLookup: DynamicSpanSizeLookup

    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var userProfileAdapter: UserProfileAdapter

    private lateinit var resultNotFound: LinearLayout

    private var userProfileList: MutableList<UserProfile> = mutableListOf()
    private var recentSearchList: MutableList<RecentSearch> = mutableListOf()

    private lateinit var tokenManager: TokenManager

    //    private lateinit var userProfileViewModel:UserProfileViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var recentSearchViewModel: RecentSearchViewModel
    private lateinit var profileBookmarkViewModel: ProfileBookmarkViewModel

    private var currentPage = 0

    private var currentUser: UserDto? = null

    private var isRecentSearchFetched: Boolean = false

    private val userProfileViewModel: UserProfileViewModel by lazy {
        ViewModelProvider(
            this,
            UserProfileViewModelFactory(tokenManager, requireContext())
        )[UserProfileViewModel::class.java]
    }


    private lateinit var networkManager: NetworkManager
    private lateinit var customErrorSnackBar: CustomErrorSnackBar
    private var isConnectedToInternet: Boolean = true
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_search, container, false)

        networkManager = NetworkManager(requireContext())           // initializing network manager

        checkForInternetConnection()

        //      initializing customErrorSnackBar
        customErrorSnackBar = CustomErrorSnackBar()
        tokenManager = TokenManager(requireContext())

        currentUser = CommonFunction.SharedPrefsUtil.fetchUserResponseFromSharedPreferences()

//        userProfileViewModel=ViewModelProvider(this, UserProfileViewModelFactory(tokenManager,requireContext()))[UserProfileViewModel::class.java]
        notificationViewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(tokenManager)
        )[NotificationViewModel::class.java]
        recentSearchViewModel = ViewModelProvider(
            this,
            RecentSearchViewModelFactory(tokenManager)
        )[RecentSearchViewModel::class.java]
        profileBookmarkViewModel = ViewModelProvider(
            this,
            ProfileBookmarkViewModelFactory(tokenManager)
        )[ProfileBookmarkViewModel::class.java]



        recentSearchAdapter = RecentSearchAdapter(recentSearchList)

        userProfileAdapter = UserProfileAdapter(ArrayList(), currentUser!!.isRecuriter == 1)


        recentSearchRecyclerView = rootView.findViewById(R.id.search_fragment_recent_searches)
        userProfileRecyclerView = rootView.findViewById(R.id.search_fragment_recyclerView)
        searchView = rootView.findViewById(R.id.search_bar_edittext)
        recentSearchContainer =
            rootView.findViewById(R.id.search_fragment_recent_searches_container)
        recentSearchClearAll = rootView.findViewById(R.id.search_fragment_recent_searches_clear_all)
        searchLoadingAnimation = rootView.findViewById(R.id.search_fragment_loading_animation)


        searchView.addTextChangedListener(searchQueryTextWatcher)

//        filter
        filterContainer = rootView.findViewById(R.id.search_filter_layout)
        filterButton = rootView.findViewById(R.id.search_bar_filter_icon)
        filterRecyclerView = rootView.findViewById(R.id.search_filter_recyclerView)

        filterAdapter =
            FilterAdapter(listOf("Experience level", "Category", "Location"), requireContext())
        filterRecyclerView.adapter = filterAdapter
        filterRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        recentSearchRecyclerView.adapter = recentSearchAdapter
        recentSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        resultNotFound = rootView.findViewById(R.id.search_user_result_not_found)

        userProfileRecyclerView.adapter = userProfileAdapter
        userProfileRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        if (!isRecentSearchFetched && isConnectedToInternet) {
            recentSearchContainer.visibility = View.VISIBLE

            viewLifecycleOwner.lifecycleScope.launch {
                recentSearchViewModel.getAllRecentSearches(currentUser!!.id)
            }
            recentSearchAdapter.setOnDeleteIconClickListener(object :
                RecentSearchAdapter.OnDeleteIconClickListener {
                override fun onDeleteIconClick(position: Int) {

                        val recentSearch = recentSearchList[position]
                        recentSearchList.removeAt(position)

                        if (recentSearchList.size == 0) {
                            recentSearchContainer.visibility = View.GONE
                        }
                        recentSearchAdapter.notifyItemRemoved(position)

                    viewLifecycleOwner.lifecycleScope.launch {
                        recentSearchViewModel.deleteRecentSearch(recentSearch.id)
                    }


                }
            })

            recentSearchAdapter.setOnItemClickListener(object :
                RecentSearchAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val item = recentSearchList[position]
                    searchView.setText(item.searchedText)
                    recentSearchAdapter.notifyItemRemoved(position)
                    recentSearchContainer.visibility = View.GONE
                }

            })

            userProfileAdapter.setOnItemClickListener(object :
                UserProfileAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, userProfile: UserProfile) {

                    sendNotification(userProfile)
                    val intent = Intent(requireContext(), ProfileActivity::class.java)
                    intent.putExtra("user", convertUserToUserDto(userProfile))
                    startActivity(intent)
                }


            })


            recentSearchClearAll.setOnClickListener {
                deleteAllRecentSearches()
            }

                userProfileAdapter.setOnLoadMoreListener(object :
                    UserProfileAdapter.OnLoadMoreListener {
                    override fun onLoadMore() {
                        if (userProfileAdapter.getIsLoading()) {
                            loadMoreData()
                        }

                    }

                })



            filterAdapter.setOnItemClickListener(object : FilterAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    openFilterBottomSheet()
                }

            })

            recentSearchViewModel.recentSearchList.observe(viewLifecycleOwner) { recentSearches ->
                if (!recentSearches.isNullOrEmpty()) {
                    recentSearchContainer.visibility=View.VISIBLE
                    isRecentSearchFetched = true
                    recentSearchList.clear()
                    recentSearchList.addAll(recentSearches)
                    recentSearchAdapter.updateList(recentSearchList)
                }else{
                    recentSearchContainer.visibility=View.GONE
                }

            }

            recentSearchViewModel.createdRecentSearch.observe(viewLifecycleOwner) { addedRecentSearch ->
                if (addedRecentSearch != null) {
                    recentSearchList.add(0, addedRecentSearch) // Add to the top of the list
                    recentSearchAdapter.notifyItemInserted(0)
                }
            }
            recentSearchViewModel.loading.observe(viewLifecycleOwner){isLoading->

            }



        }
        return rootView
    }

    private fun openFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_option_bottomsheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetLayout)
        bottomSheetDialog.show()


        val recyclerView =
            bottomSheetDialog.findViewById<RecyclerView>(R.id.filter_option_bottomSheet_recyclerView)
        val item1 = Item(1, "Fresher", 1)
        val item2 = Item(2, "Intermediate", 1)
        val item3 = Item(3, "Experienced", 1)
        val item4 = Item(4, "Student Intermediate", 1)
        val item5 = Item(0, "FreeLancer", 1)
        val items = listOf(item1, item2, item3, item4, item5, item1, item4, item2)
        filterItemAdapter = FilterItemAdapter(requireContext(), items)
        recyclerView!!.adapter = filterItemAdapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        // Set up DynamicSpanSizeLookup
        dynamicSpanSizeLookup = DynamicSpanSizeLookup(items)
        layoutManager.spanSizeLookup = dynamicSpanSizeLookup


    }


    private val searchQueryTextWatcher = object : TextWatcher {
        private val DELAY = 1000L // Delay in milliseconds
        private val handler = Handler(Looper.getMainLooper())

        private var lastQuery = ""
        private var isLoading = false // Variable to track loading state

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            handler.removeCallbacksAndMessages(null)
            lastQuery = s.toString()

            // Delay the API call
            handler.postDelayed({
                // Check if the user has not typed anything new
                if (lastQuery == s.toString()) {
                    val searchQuery = s.toString()
                    if (searchQuery.isEmpty()) {
                        if (recentSearchList.isNotEmpty()) {
                            recentSearchContainer.visibility = View.VISIBLE
                        } else {
                            recentSearchContainer.visibility = View.GONE
                        }

                        userProfileRecyclerView.visibility = View.GONE
                        resultNotFound.visibility = View.GONE
                        filterContainer.visibility = View.GONE
                    } else {
                        if (isConnectedToInternet) {
                            recentSearchContainer.visibility = View.GONE
                            userProfileRecyclerView.visibility = View.VISIBLE

                            searchLoadingAnimation.visibility = View.GONE


                            val recentSearch = RecentSearch(0, searchQuery, currentUser!!.id)
                            val existingSearch =
                                recentSearchList.find { it.searchedText == recentSearch.searchedText }
                            if (existingSearch == null) {

                                // Update the adapter here or use notifyDataSetChanged() if needed
                                viewLifecycleOwner.lifecycleScope.launch{
                                    recentSearchViewModel.addRecentSearch(recentSearch)
                                }

                            }

                            userProfileViewModel.getAllCandidateProfile(
                                searchQuery.trimEnd().trimStart(), "",
                                currentPage,
                                8,
                                "id",
                                "ASC"
                            )
                                .observe(requireActivity()) {
                                    // Search completed, hide loading animation
                                    searchLoadingAnimation.visibility = View.GONE
                                    isLoading = false

                                    if (it.isEmpty()) {
                                        resultNotFound.visibility = View.VISIBLE
                                        filterContainer.visibility = View.GONE
                                    } else {
                                        filterContainer.visibility = View.VISIBLE
                                        resultNotFound.visibility = View.GONE
                                        recentSearchRecyclerView.visibility = View.VISIBLE
                                        userProfileAdapter.setLoading(true)
                                    }
                                    userProfileList.clear()
                                    userProfileList.addAll(it)
                                    userProfileAdapter.updateList(userProfileList, true)

                                }
                        } else {

                            showCustomErrorSnackBar()
                        }
                    }

                }
            }, DELAY)
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun deleteAllRecentSearches() {
        CoroutineScope(Dispatchers.IO).launch {
            recentSearchContainer.visibility = View.GONE
            recentSearchList.clear()
            recentSearchAdapter.updateList(mutableListOf())
            recentSearchViewModel.deleteAllRecentSearches(currentUser!!.id)

        }
    }

    private fun sendNotification(userProfile: UserProfile) {
        val request = NotificationRequest(
            currentUser!!.username,
            "Profile view",
            "Your Profile is viewed by someone",
            userProfile.username,
            Lists().notificationPref[2]
        )
        if (currentUser?.username?.equals(userProfile.username) == false) {
            CoroutineScope(Dispatchers.IO).launch {
                notificationViewModel.sendNotification(request)
            }
        }
    }

    private fun loadMoreData() {

        currentPage++
        userProfileAdapter.setLoading(false)

        // Fetch more results with the same search query and location
        userProfileViewModel.getAllCandidateProfile(
            searchView.text.toString().trimEnd().trimStart(),
            "",
            currentPage,
            8,
            "id",
            "ASC"
        )
            .observe(requireActivity()) {
                // Update recycler view
                userProfileAdapter.updateList(it, false)

                // Set loading flag to false
                if (it.size == 8) {
                    userProfileAdapter.setLoading(true)
                } else {
                    userProfileAdapter.setLoading(false)
                }
            }

    }


    private fun onProfileClick(position: Int) {
        // Handle profile click based on position and data
        val userProfile = userProfileList[position]

        sendNotification(userProfile)
        // ... Implement your specific click logic ...
    }

    private fun showCustomErrorSnackBar() {

        customErrorSnackBar.showSnackbar(
            requireContext(),
            requireView(),
            "No internet connection"
        )

    }

    private fun checkForInternetConnection() {

        lifecycleScope.launch(Dispatchers.Main) { // Launch coroutine on Main dispatcher
            networkManager.getNetworkConnectivityFlow()
                .flowOn(Dispatchers.IO) // Perform network operations on IO dispatcher
                .collect {   // Collect flow on Main dispatcher
                    // Update UI or perform actions based on network state
                    isConnectedToInternet = it.isConnected
                    if (!isConnectedToInternet) {
                        showCustomErrorSnackBar()
                    }
                }

        }

    }

    private fun convertUserToUserDto(user: UserProfile): UserDto {
        return UserDto(
            id = user.id,
            birthdate = user.birthdate,
            currentOccupation = user.currentOccupation,
            email = user.email,
            headline = user.headline,
            name = user.name,
            phone = user.phone,
            status = user.status,
            username = user.username,
            isRecuriter = user.isRecuriter, // Set this value based on your logic
            gender = null // Set this value based on your logic
        )
    }

}




