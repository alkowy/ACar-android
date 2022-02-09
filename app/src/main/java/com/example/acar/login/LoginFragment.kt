package com.example.acar.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.acar.R
import com.example.acar.common.GlobalToast
import com.example.acar.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickRegisterText()
        onClickLoginButton()
        observeLoginStatus()
        fakeLogin()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = LoginFragmentBinding.inflate(layoutInflater)
        navController = findNavController()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickRegisterText() {
        binding.loginRegisterYourAccountText.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun onClickLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailText.text.toString()
            val password = binding.loginPasswordText.text.toString()
            when {
                email.isEmpty() -> GlobalToast.showShort(context, "E-mail is required")
                password.isEmpty() -> GlobalToast.showShort(context, "Password is required")
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun observeLoginStatus() {
        viewModel.isLoginSuccessful.observe(viewLifecycleOwner) {
            if (it) {
                navController.navigate(R.id.action_loginFragment_to_orderFragment)
                viewModel.doneNavigatingAfterSuccessfulLogin()
            }
            else {
                viewModel.failedLoginMessage.observe(viewLifecycleOwner) {
                    GlobalToast.showLong(context, it.toString())
                }
            }
        }
    }

    private fun fakeLogin() {
        binding.testLoginBtn.setOnClickListener {
            viewModel.login("test@gmail.com", "123456")
        }
    }
}