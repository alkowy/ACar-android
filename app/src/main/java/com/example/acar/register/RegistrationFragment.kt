package com.example.acar.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.acar.R
import com.example.acar.common.GlobalToast
import com.example.acar.databinding.RegistrationFragmentBinding
import com.example.acar.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()
    private var _binding: RegistrationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   observeIsRegistrationSuccessful()
        onClickRegisterButton()
        binding.registerBackToLoginText.setOnClickListener {
            navController.navigate(R.id.action_registrationFragment_to_loginFragment)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RegistrationFragmentBinding.inflate(layoutInflater)
        navController = findNavController()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickRegisterButton() {
        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailText.text.toString()
            val passwordFirst = binding.registerPasswordFirst.text.toString()
            val passwordSecond = binding.registerPasswordSecond.text.toString()
            when {
                email.isEmpty() -> GlobalToast.showShort(context, "E-mail is required")
                passwordFirst.isBlank() -> GlobalToast.showShort(context, "Password is required")
                passwordFirst != passwordSecond -> GlobalToast.showShort(context, "Password mismatch")
                else -> {
                    viewModel.register(email, passwordFirst)
                    observeIsRegistrationSuccessful()
                }
            }
        }
    }

    private fun observeIsRegistrationSuccessful() {
        viewModel.isRegistrationSuccessful.observe(viewLifecycleOwner) {
            if (it) {
                Navigation.findNavController(binding.root).navigate(R.id.action_registrationFragment_to_loginFragment)
                GlobalToast.showShort(context, "Registration successful")
                viewModel.doneNavigatingAfterRegistration()
            }
            else {
                viewModel.registrationFailedMessage.observe(viewLifecycleOwner) {
                    GlobalToast.showLong(context, it.toString())
                }
            }
        }
    }


}