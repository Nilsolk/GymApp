package ru.nilsolk.gymapp.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentProfileBinding
import ru.nilsolk.gymapp.repository.network.FirebaseAuthService
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.ui.authentication.AuthenticationActivity
import ru.nilsolk.gymapp.ui.home.MainViewModel
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.utils.CustomProgress
import ru.nilsolk.gymapp.utils.downloadImageFromURL
import java.util.Locale
import kotlin.math.abs

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuthService: FirebaseAuthService
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private lateinit var appPreferences: AppPreferences
    private lateinit var mainViewModel: MainViewModel
    private lateinit var customProgress: CustomProgress

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE
        fragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)
        appPreferences = AppPreferences(requireContext())
        firebaseAuthService = FirebaseAuthService(requireContext())

        return fragmentProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this@ProfileFragment)[MainViewModel::class.java]
        mainViewModel.getProfileDetail()
        customProgress = CustomProgress(requireActivity())
        customProgress.show()

        observeProfileDetail()
        var backgroundDrawable: Drawable?
        with(fragmentProfileBinding) {

            editProfileLayout.setOnClickListener {
                backgroundDrawable = applyAlphaToDrawableWithDelay(
                    requireContext(),
                    R.drawable.kilogram_background,
                    0.7,
                    1.0,
                    200
                )
                it.background = backgroundDrawable
                val editProfileAction =
                    ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment()
                Navigation.findNavController(requireView()).navigate(editProfileAction)
            }

            statisticLayout.setOnClickListener {
                backgroundDrawable = applyAlphaToDrawableWithDelay(
                    requireContext(),
                    R.drawable.kilogram_background,
                    0.7,
                    1.0,
                    200
                )
                it.background = backgroundDrawable

                val action = ProfileFragmentDirections.actionProfileFragmentToStatisticFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
            calculatorsLayout.setOnClickListener {
                backgroundDrawable = applyAlphaToDrawableWithDelay(
                    requireContext(),
                    R.drawable.kilogram_background,
                    0.7,
                    1.0,
                    200
                )
                it.background = backgroundDrawable

                val action =
                    ProfileFragmentDirections.actionProfileFragmentToCalculatorFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }

            languageLayout.setOnClickListener {
                backgroundDrawable = applyAlphaToDrawableWithDelay(
                    requireContext(),
                    R.drawable.kilogram_background,
                    0.7,
                    1.0,
                    200
                )
                it.background = backgroundDrawable
            }

            logoutLayout.setOnClickListener {

                backgroundDrawable = applyAlphaToDrawableWithDelay(
                    requireContext(),
                    R.drawable.kilogram_background,
                    0.7,
                    1.0,
                    200
                )
                it.background = backgroundDrawable

                firebaseAuthService.signOut()
                appPreferences.removeKey("rememberMe")
                val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }
        }
    }


    private fun applyAlphaToDrawableWithDelay(
        context: Context, drawableResId: Int, initialAlpha: Double, finalAlpha: Double,
        delayMillis: Long
    ): Drawable? {
        val backgroundDrawable = ContextCompat.getDrawable(context, drawableResId)?.mutate()
        backgroundDrawable?.colorFilter = PorterDuffColorFilter(
            Color.parseColor("#232529"),
            PorterDuff.Mode.SRC_ATOP
        )
        backgroundDrawable?.mutate()?.alpha = (initialAlpha * 255).toInt()
        Handler(Looper.getMainLooper()).postDelayed({
            backgroundDrawable?.mutate()?.alpha = (finalAlpha * 255).toInt()
        }, delayMillis)
        return backgroundDrawable
    }

    private fun observeProfileDetail() {
        mainViewModel.profileDetails.observe(requireActivity()) { userProfileDetails ->
            customProgress.dismiss()
            if (userProfileDetails != null) {
                fragmentProfileBinding.rootLinearForProfile.visibility = View.VISIBLE
                fragmentProfileBinding.profileImage.downloadImageFromURL(userProfileDetails.profileImageURL.toString())
                fragmentProfileBinding.usernameAndAgeText.text =
                    "${userProfileDetails.username} , ${userProfileDetails.age}"
                fragmentProfileBinding.userEmailText.text = "${userProfileDetails.email}"
                fragmentProfileBinding.startWeightText.text = "${userProfileDetails.weight}"
                fragmentProfileBinding.targetWeightText.text = "${userProfileDetails.targetWeight}"
                fragmentProfileBinding.weightDifferencesText.text = calculateWeightDifference(
                    userProfileDetails.targetWeight!!,
                    userProfileDetails.weight!!
                )
                if (Locale.getDefault().language == "ru") {
                    fragmentProfileBinding.goalText.text =
                        TranslationConstants.mapEnglishToRussianGoal[userProfileDetails.goal]
                            ?: userProfileDetails.goal
                } else
                    fragmentProfileBinding.goalText.text = "${userProfileDetails.goal}"
            }
        }
    }

    private fun calculateWeightDifference(targetWeight: Double, startWeight: Double): String {
        val differences = abs(targetWeight - startWeight)
        return formatDoubleUsingSplit(differences, 2)
    }

    private fun formatDoubleUsingSplit(doubleValue: Double, decimalPlaces: Int): String {
        val stringValue = doubleValue.toString()
        val parts = stringValue.split(".")

        return if (parts.size > 1 && parts[1].length > decimalPlaces) {
            "${parts[0]}.${parts[1].substring(0, decimalPlaces)}"
        } else {
            stringValue
        }
    }

}