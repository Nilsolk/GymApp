package ru.nilsolk.gymapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentHomeBinding
import ru.nilsolk.gymapp.repository.model.ArticleModel
import ru.nilsolk.gymapp.utils.CustomProgress
import ru.nilsolk.gymapp.utils.StreakManager
import ru.nilsolk.gymapp.translation.TextTranslator
import ru.nilsolk.gymapp.utils.downloadImageFromURL

class HomeFragment : Fragment() {

    private var currentStreak: Int = 0

    private lateinit var article: ArrayList<ArticleModel>
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var customProgress: CustomProgress
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

        customProgress = CustomProgress(requireActivity())
        customProgress.show()

        article = ArrayList()
        articleAdapter = ArticleAdapter(article)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.getProfileDetail()
        mainViewModel.getArticles()

        search()

        observeProfileDetail()
        observeArticle()

        disableSeekBar()
        getStreak()

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHomeBinding.articleRecycler.adapter = articleAdapter

    }

    private fun getStreak() = with(fragmentHomeBinding) {
        StreakManager.getCurrentStreak(requireContext()) { streakCounter ->
            if (streakCounter != null) {
                currentStreak = streakCounter
                circularSeekBar.progress = currentStreak.toFloat()
                streakCounterText.text = currentStreak.toString()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disableSeekBar() {
        fragmentHomeBinding.circularSeekBar.setOnTouchListener { _, _ -> true }
    }

    private fun chips(categories: ArrayList<String>) = with(fragmentHomeBinding) {

        val textTranslator = TextTranslator()
        for (chipText in categories) {
            val chip = Chip(requireContext())

            chip.text = chipText
            chip.isCheckable = true
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(requireContext(), R.color.chip_background)
            chip.setChipStrokeColorResource(R.color.light_gray)
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChip = group.findViewById<Chip>(group.checkedChipId)

            if (selectedChip != null) {
                if (selectedChip.id == R.id.chipAll) {
                    articleAdapter.setData(article)
                } else {
                    val category = selectedChip.text.toString()
                    val filteredArticles = article.filter { it.category == category }
                    articleAdapter.setData(filteredArticles)
                }
            } else {
                chipAll.isChecked = true
            }

        }
    }

    private fun search() = with(fragmentHomeBinding) {
        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString().trim()

                val filteredArticles =
                    article.filter { it.title.contains(searchText, ignoreCase = true) }
                articleAdapter.setData(filteredArticles)

                chipAll.isChecked = true

                emptyText.visibility = if (filteredArticles.isEmpty()) View.VISIBLE
                else View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
    }

    private fun observeProfileDetail() = with(fragmentHomeBinding) {
        mainViewModel.profileDetails.observe(requireActivity()) { userProfileDetails ->
            customProgress.dismiss()
            if (userProfileDetails != null) {
                homeRootLinear.visibility = View.VISIBLE
                profileImage.downloadImageFromURL(userProfileDetails.profileImageURL.toString())
                nicknameText.text =
                    "${userProfileDetails.username} ${getString(R.string.wave_hand)}"
                Log.d("dal", profileImage.toString())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeArticle() {
        mainViewModel.articleLiveData.observe(requireActivity()) { resultArticle ->
            if (resultArticle != null) {
                article.clear()
                article.addAll(resultArticle)
                articleAdapter.notifyDataSetChanged()
                val categories = resultArticle.map { it.category }.distinct()
                chips(ArrayList(categories))
            }
        }
    }

}