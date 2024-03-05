package com.emirsansar.catchthekotlingame.view.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.FragmentProfileBinding
import com.emirsansar.catchthekotlingame.view.login.LoginActivity
import com.emirsansar.catchthekotlingame.viewmodel.UserProfileViewModel
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso
import java.io.IOException


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var auth: FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var userEmail: String

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var selectedBitmap : Bitmap? = null

    private lateinit var viewModelUserRecord : UserRecordViewModel
    private lateinit var viewModelUserProfile: UserProfileViewModel

    private val fromRotate: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_rotate) }
    private val toRotate: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_rotate) }
    private val fromScale: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_scale) }
    private val toScale: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_scale) }

    private var clickedFab: Boolean = true

    companion object {
        var isChangedUserRecord = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser

        userEmail = currentUser!!.email.toString()

        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelUserRecord = ViewModelProvider(this)[UserRecordViewModel::class.java]
        viewModelUserProfile = ViewModelProvider(this)[UserProfileViewModel::class.java]

        populateUserProfileViews()

        setListeners()
    }



    private fun getUserScoresFromFirestore(){
        viewModelUserRecord.getUserScoreFromFirestore(userEmail, "10"){ score ->
            binding.textScoreTen.text = score.toString()

            viewModelUserRecord.updateUserScoreFor10SecOnRoom(userEmail, score!!)
        }

        viewModelUserRecord.getUserScoreFromFirestore(userEmail, "30"){ score ->
            binding.textScoreThirty.text = score.toString()

            viewModelUserRecord.updateUserScoreFor30SecOnRoom(userEmail, score!!)
        }

        viewModelUserRecord.getUserScoreFromFirestore(userEmail, "60"){ score ->
            binding.textScoreSixty.text = score.toString()

            viewModelUserRecord.updateUserScoreFor60SecOnRoom(userEmail, score!!)
        }
    }

    private fun getUserScoresFromRoomDB(){
        viewModelUserRecord.getUserRecordFromRoomDB(userEmail){ userRecord ->
            if (userRecord != null){
                binding.textScoreTen.text = userRecord.record_10Second
                binding.textScoreThirty.text = userRecord.record_30Second
                binding.textScoreSixty.text = userRecord.record_60Second
            }
        }
    }

    private fun checkAndUpdateUserScoreChange(){
        if (isChangedUserRecord){
            getUserScoresFromFirestore()
            isChangedUserRecord = false
        }
        else
            getUserScoresFromRoomDB()
    }

    private fun populateUserProfileViews(){
        checkAndUpdateUserScoreChange()

        viewModelUserProfile.getUserFullNameFromRoomDB(userEmail){ fullName ->
            binding.textFullName.text = fullName }

        viewModelUserProfile.getUserProfilePictureUriFromRoomDB(userEmail){ uri ->
            if (uri.toString() != "")
                Picasso.get().load(uri).into(binding.viewProfilePicture)
            else {
                binding.viewProfilePicture.setImageResource(R.drawable.default_profile_picture)
            }
        }
    }

    private fun selectProfilePictureFromGallery(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(requireView(), "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(requireView(), "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data

                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                            binding.viewProfilePicture.setImageBitmap(selectedBitmap)
                            uploadPictureToFirebaseStorage()
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)

                            binding.viewProfilePicture.setImageBitmap(selectedBitmap)
                            uploadPictureToFirebaseStorage()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showChangeNameDialog(){
        val nameDialogView = layoutInflater.inflate(R.layout.name_dialog_layout, null)
        val inputName = nameDialogView.findViewById<EditText>(R.id.inputName)
        val inputSurname = nameDialogView.findViewById<EditText>(R.id.inputSurname)

        val nameDialogBuilder = AlertDialog.Builder(requireContext())
        nameDialogBuilder.setTitle("Enter your name.")
        nameDialogBuilder.setView(nameDialogView)
        nameDialogBuilder.setPositiveButton("Okay") { _, _ ->
            val newName = inputName.text.toString()
            val newSurname = inputSurname.text.toString()

            if (newName.isNotEmpty() && newSurname.isNotEmpty()){
                viewModelUserProfile.updateUserFullNameOnFirestore(userEmail, newName, newSurname){ boolean ->
                    if (boolean){
                        viewModelUserProfile.updateFullNameOnRoomDB(userEmail, "$newName $newSurname")

                        val fullName = getString(R.string.full_name, newName, newSurname)
                        binding.textFullName.text = fullName
                    }
                    else
                        Toast.makeText(requireContext(),"An error occurred while updating your name.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        nameDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        nameDialogBuilder.show()
    }

    private fun uploadPictureToFirebaseStorage(){
        if (selectedBitmap != null){
            viewModelUserProfile.uploadProfilePictureToFirebaseStorage(userEmail, selectedBitmap!!){ uri, isUploaded ->
                if (isUploaded) {
                    viewModelUserProfile.updateUserProfilePictureUriInRoomDB(userEmail, uri.toString())
                } else {
                    binding.viewProfilePicture.setImageResource(R.drawable.default_profile_picture)
                }
            }
        }
    }

    private fun isAnimation(clicked: Boolean){
        if (clicked){
            binding.fabMore.startAnimation(fromRotate)
            binding.fabEditProfile.startAnimation(fromScale)
            binding.fabLogOut.startAnimation(fromScale)
        } else {
            binding.fabMore.startAnimation(toRotate)
            binding.fabEditProfile.startAnimation(toScale)
            binding.fabLogOut.startAnimation(toScale)
        }
    }

    private fun isClick(){
        isAnimation(clickedFab)
        controlVisibilityOfFabs(clickedFab)
        clickedFab =! clickedFab
    }

    private fun controlVisibilityOfFabs(clicked: Boolean){
        if (clicked){
            binding.fabEditProfile.visibility = View.INVISIBLE
            binding.fabLogOut.visibility = View.INVISIBLE
        } else {
            binding.fabEditProfile.visibility = View.VISIBLE
            binding.fabLogOut.visibility = View.VISIBLE
        }
    }

    private fun setFabMoreListener(){
        binding.fabMore.setOnClickListener {
            isClick()
        }
    }

    private fun setFabEditProfileListener(){
        binding.fabEditProfile.setOnClickListener {
            isClick()

            val options = arrayOf("Change your name", "Change the profile picture")

            val mainDialog = AlertDialog.Builder(requireContext())
            mainDialog.setTitle("Options")
            mainDialog.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        showChangeNameDialog()
                    }
                    1 -> {
                        selectProfilePictureFromGallery()
                        uploadPictureToFirebaseStorage()
                    }
                }
            }

            val dialog = mainDialog.create()
            dialog.show()
        }
    }

    private fun setFabLogOutListener(){
        binding.fabLogOut.setOnClickListener {
            isClick()

            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of your account?")
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun setViewProfilePictureListener(){
        binding.viewProfilePicture.setOnClickListener{
            val options = arrayOf("Remove Profile Picture")

            val mainDialog = AlertDialog.Builder(requireContext())
            mainDialog.setTitle("Options")
            mainDialog.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        removeProfilePicture()
                    }
                }
            }

            val dialog = mainDialog.create()
            dialog.show()
        }
    }

    private fun removeProfilePicture(){
        viewModelUserProfile.removeProfilePictureFromStorage(userEmail){ isRemoved ->
            if (isRemoved){
                binding.viewProfilePicture.setImageResource(R.drawable.default_profile_picture)
                viewModelUserProfile.updateUserProfilePictureUriInRoomDB(userEmail, "")
            }
        }
    }

    private fun setListeners(){
        setFabMoreListener()
        setFabEditProfileListener()
        setFabLogOutListener()
        setViewProfilePictureListener()
    }

}