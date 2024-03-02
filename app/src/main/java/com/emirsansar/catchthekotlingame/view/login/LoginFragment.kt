package com.emirsansar.catchthekotlingame.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.databinding.FragmentLoginBinding
import com.emirsansar.catchthekotlingame.view.main.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }


    private fun login(){
        val email = binding.textEmail.text.toString()
        val password = binding.textPassword.text.toString()

        if (email.isEmpty() || password.isEmpty() ){
            binding.textError.text = "Lütfen tüm alanları doldurunuz!"
        } else if (!isValidEmail(email)) {
            binding.textError.text = "Mail adresiniz geçersiz,lütfen geçerli bir adres değil!"
        } else {

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity((intent))
                requireActivity().finish()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Giriş yaparken hata oluştu: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setBtnLoginListener(){
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            login()
        }
    }

    private fun setRegisterFragmentListener(){
        binding.textViewClickable.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setListeners(){
        setBtnLoginListener()
        setRegisterFragmentListener()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
