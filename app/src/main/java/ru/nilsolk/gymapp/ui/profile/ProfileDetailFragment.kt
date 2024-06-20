package ru.nilsolk.gymapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentProfileDetailBinding
import ru.nilsolk.gymapp.repository.network.FirebaseAuthService
import ru.nilsolk.gymapp.repository.network.FirebaseFirestoreService
import ru.nilsolk.gymapp.repository.network.FirebaseStorageService
import ru.nilsolk.gymapp.ui.MainActivity
import ru.nilsolk.gymapp.utils.CustomProgress
import ru.nilsolk.gymapp.utils.PermissionManager

class ProfileDetailFragment : Fragment() {

    private var age: Int? = null
    private var height: Int? = null
    private var weight: Double? = null
    private var targetWeight: Double? = null
    private var goal: String? = null
    private var activityLevel: String? = null
    private var imageURI: Uri? = null


    private lateinit var customProgress: CustomProgress
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>
    private lateinit var permissionManager: PermissionManager
    private lateinit var firebaseAuthService: FirebaseAuthService
    private lateinit var firebaseStorageService: FirebaseStorageService
    private lateinit var firebaseFirestoreService: FirebaseFirestoreService
    private lateinit var fragmentProfileDetailBinding: FragmentProfileDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileDetailBinding = FragmentProfileDetailBinding.inflate(layoutInflater)

        customProgress = CustomProgress(requireContext())
        permissionManager = PermissionManager(requireContext())
        firebaseAuthService = FirebaseAuthService(requireContext())
        firebaseStorageService = FirebaseStorageService(requireContext())
        firebaseFirestoreService = FirebaseFirestoreService(requireContext())

        levelActivityDropDownSettings()
        goalsDropDownSettings()
        galleryResult()

        return fragmentProfileDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fragmentProfileDetailBinding) {

            profileImage.setOnClickListener {
                if (permissionManager.checkStoragePermission()) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    resultLauncher.launch(intent)
                } else {
                    permissionManager.requestStoragePermission(requireActivity())
                }
            }

            goalDropDown.setOnItemClickListener { adapterView, view, i, l ->
                goal = adapterView.getItemAtPosition(i).toString()
            }
            activityLevelDropDown.setOnItemClickListener { parent, view, position, id ->
                activityLevel = parent.getItemAtPosition(position).toString()
            }

            startButton.setOnClickListener {
                if (ageText.text.isNullOrEmpty() || heightText.text.isNullOrEmpty() || weightText.text.isNullOrEmpty() ||
                    targetWeightText.text.isNullOrEmpty() || goal.isNullOrEmpty() || activityLevel.isNullOrEmpty() || imageURI == null
                ) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_fill_in_the_empty_fields),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    customProgress.show()
                    firebaseStorageService.uploadImage(imageURI!!) { downloadUrl ->
                        if (!downloadUrl.isNullOrEmpty()) {
                            age = ageText.text.toString().toInt()
                            height = heightText.text.toString().toInt()
                            weight = weightText.text.toString().toDouble()
                            targetWeight = targetWeightText.text.toString().toDouble()
                            val updateMap = hashMapOf<String, Any>(
                                "age" to age!!,
                                "height" to height!!,
                                "weight" to weight!!,
                                "targetWeight" to targetWeight!!,
                                "goal" to goal!!,
                                "levelActivity" to activityLevel!!,
                                "profileImageURL" to downloadUrl
                            )
                            firebaseFirestoreService.updateProfileDetail(
                                firebaseAuthService.getCurrentUserEmail(),
                                updateMap
                            ) { result ->
                                if (result) {
                                    customProgress.dismiss()
                                    requireActivity().finish()
                                    val intent = Intent(requireActivity(), MainActivity::class.java)
                                    startActivity(intent)
                                } else customProgress.dismiss()
                            }
                        } else {
                            customProgress.dismiss()
                        }
                    }
                }
            }

        }

    }

    private fun goalsDropDownSettings() {
        val goalItems = listOf(
            getString(R.string.weightLoss),
            getString(R.string.weightGain),
            getString(R.string.increaseEndurance),
            getString(R.string.buildMuscleMass),
            getString(R.string.improveFlexibility)
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_items, goalItems)
        fragmentProfileDetailBinding.goalDropDown.setAdapter(adapter)
    }

    private fun levelActivityDropDownSettings() {
        val goalItems = listOf(
            getString(R.string.beginner),
            getString(R.string.middle),
            getString(R.string.advanced)
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_items, goalItems)
        fragmentProfileDetailBinding.activityLevelDropDown.setAdapter(adapter)
    }

    private fun galleryResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    imageURI = it.data?.data
                    fragmentProfileDetailBinding.profileImage.setImageURI(imageURI)
                }
            }
    }


}