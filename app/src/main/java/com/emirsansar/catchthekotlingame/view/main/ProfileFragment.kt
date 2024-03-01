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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.FragmentProfileBinding
import com.emirsansar.catchthekotlingame.model.UserRecord
import com.emirsansar.catchthekotlingame.view.login.LoginActivity
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser : FirebaseUser? = null
    private lateinit var storage: FirebaseStorage

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var selectedBitmap : Bitmap? = null

    private lateinit var viewModel : UserRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth
        currentUser = auth.currentUser
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

        viewModel = ViewModelProvider(this)[UserRecordViewModel::class.java]

        currentUser?.email.toString().let { email ->
            viewModel.downloadImageFromFirebaseStorage(email){ uri ->
                Picasso.get().load(uri).into(binding.profilePicture)
            }

            viewModel.getUserFullName(email){fullName ->
                binding.textFullName.text = fullName
            }

            getUserScores(email)

            registerLauncher(currentUser!!.email.toString())
        }

        setListeners()
    }


    private fun getUserScores(userEmail: String){
        viewModel.getUserScoreFor10Second(userEmail){ score ->
            binding.textScoreTen.text = score.toString()
        }
        viewModel.getUserScoreFor30Second(userEmail){ score ->
            binding.textScoreThirty.text = score.toString()
        }
        viewModel.getUserScoreFor60Second(userEmail){ score ->
            binding.textScoreSixty.text = score.toString()
        }
    }

    private fun changeProfilePicture(){
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

    private fun registerLauncher(userEmail: String){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data

                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                            binding.profilePicture.setImageBitmap(selectedBitmap)

                            //viewModel.uploadImageToFirebaseStorage(selectedBitmap!!, userEmail)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)

                            binding.profilePicture.setImageBitmap(selectedBitmap)

                            //viewModel.uploadImageToFirebaseStorage(selectedBitmap!!, userEmail)
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

    private fun buildChangeNameDialog(){
        val nameDialogView = layoutInflater.inflate(R.layout.name_dialog_layout, null)
        val inputName = nameDialogView.findViewById<EditText>(R.id.inputName)
        val inputSurname = nameDialogView.findViewById<EditText>(R.id.inputSurname)

        val nameDialogBuilder = AlertDialog.Builder(requireContext())
        nameDialogBuilder.setTitle("Enter your name.")
        nameDialogBuilder.setView(nameDialogView)
        nameDialogBuilder.setPositiveButton("Okay") { dialog, which ->
            val newName = inputName.text.toString()
            val newSurname = inputSurname.text.toString()

            if (newName.isNotEmpty() && newSurname.isNotEmpty()){
                viewModel.changeUserFullName(currentUser!!.email.toString(), newName, newSurname)
                binding.textFullName.text = "$newName $newSurname"
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
        nameDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        nameDialogBuilder.show()
    }

    private fun isClickedEditButton(boolean: Boolean){
        if (boolean){
            binding.btnEditProfile.visibility = View.GONE
            binding.btnConfirmPicture.visibility = View.VISIBLE
            binding.btnCancelPicture.visibility = View.VISIBLE
        } else {
            binding.btnEditProfile.visibility = View.VISIBLE
            binding.btnConfirmPicture.visibility = View.GONE
            binding.btnCancelPicture.visibility = View.GONE
        }
    }


    private fun setBtnEditListener(){
        binding.btnEditProfile.setOnClickListener {
            val options = arrayOf("Change your name", "Change the profile picture")

            val mainDialog = AlertDialog.Builder(requireContext())
            mainDialog.setTitle("Options")
            mainDialog.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        buildChangeNameDialog()
                    }
                    1 -> {
                        changeProfilePicture()
                        isClickedEditButton(true)
                    }
                }
            }

            val dialog = mainDialog.create()
            dialog.show()
        }
    }

    private fun setBtnConfirmListener(){
        binding.btnConfirmPicture.setOnClickListener {
            isClickedEditButton(false)
            viewModel.uploadImageToFirebaseStorage(selectedBitmap!!, currentUser!!.email.toString())
        }
    }

    private fun setBtnCancelListener(){
        binding.btnCancelPicture.setOnClickListener {
            isClickedEditButton(false)
            viewModel.downloadImageFromFirebaseStorage(currentUser!!.email.toString()){
                Picasso.get().load(it).into(binding.profilePicture)
            }
        }
    }

    private fun setBtnLogOutListener(){
        binding.btnLogOut.setOnClickListener {
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

    private fun setListeners(){
        setBtnEditListener()
        setBtnConfirmListener()
        setBtnCancelListener()
        setBtnLogOutListener()
    }
}