package com.emirsansar.catchthekotlingame.view.login

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.FragmentRegisterBinding
import com.emirsansar.catchthekotlingame.model.UserProfile
import com.emirsansar.catchthekotlingame.model.UserRecord
import com.emirsansar.catchthekotlingame.viewmodel.UserProfileViewModel
import com.emirsansar.catchthekotlingame.viewmodel.UserRecordViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore

    private lateinit var viewModelUserProfile: UserProfileViewModel
    private lateinit var viewModelUserRecord: UserRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelUserProfile = ViewModelProvider(this)[UserProfileViewModel::class.java]
        viewModelUserRecord = ViewModelProvider(this)[UserRecordViewModel::class.java]

        setListeners()
    }

    private fun register(){
        val name = binding.textRegisterName.text.toString()
        val surname = binding.textRegisterSurname.text.toString()
        val email = binding.textRegisterEmail.text.toString()
        val password = binding.textRegisterPassword.text.toString()
        val password2 = binding.textRegisterPassword2.text.toString()

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            binding.textError.text = "Lütfen tüm alanları doldurunuz!"
        } else {

            if (!isValidEmail(email)){
                binding.textError.text = "Mail adresiniz geçersiz,lütfen geçerli bir adres değil."
                return
            } else if (password != password2){
                binding.textError.text = "Şifreleriniz uyuşmuyor. Lütfen gözden geçiriniz."
                return
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelUserProfile.insertUserProfileToFirestore(email, name, surname){ result ->
                            if (result){
                                viewModelUserRecord.insertDataToRoomDB(UserRecord(email, "0", "0", "0"))
                                viewModelUserProfile.saveUserProfileToRoomDB(UserProfile(email, "$name $surname", ""))
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }

                    } else {
                        Toast.makeText(requireContext(), "Kullanıcı kaydı başarısız oldu: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Kullanıcı kaydı başarısız oldu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun setListeners(){
        setLoginFragmentListener()
        setBtnRegisterListener()
    }

    private fun setBtnRegisterListener(){
        binding.btnRegister.setOnClickListener {
            hideKeyboard()
            register()
        }
    }

    private fun setLoginFragmentListener(){
        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}