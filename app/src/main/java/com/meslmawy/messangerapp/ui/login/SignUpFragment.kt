package com.meslmawy.messangerapp.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.meslmawy.messangerapp.R
import com.meslmawy.messangerapp.databinding.SignUpFragmentBinding
import com.meslmawy.messangerapp.models.State
import com.meslmawy.messangerapp.models.User
import com.meslmawy.messangerapp.ui.ProgressbarLoader
import com.meslmawy.messangerapp.ui.home.HomeActivity
import java.util.*


class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var progresspar: ProgressbarLoader


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = SignUpFragmentBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        progresspar = activity?.let { ProgressbarLoader(it) }!!
        setOnclickListeners()

        viewModel.state().observe(viewLifecycleOwner, Observer { event ->
            event?.let { state ->
                when (state.status) {
                    State.Status.LOADING ->
                        progresspar.showloader()
                    State.Status.SUCCESS -> {
                        progresspar.dismissloader()
                        moveToHomeActivity()
                    }
                    State.Status.ERROR -> {
                        progresspar.dismissloader()
                        viewModel.showToast(state.error?.message.toString())
                    }
                }
            }
        })
        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        })
        return binding.root
    }

    private fun setOnclickListeners() {
        binding.signupButton.setOnClickListener {
            signuplistner()
        }
        binding.signtologTxt.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun signuplistner() {
        val user_name: String = binding.usernameEditText.text.toString().trim { it <= ' ' }
        val email: String = binding.emailEdittext.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordEdittext.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(user_name) -> viewModel.showToast("fill User Name field!!")
            TextUtils.isEmpty(email) -> viewModel.showToast("fill Email field!!")
            TextUtils.isEmpty(password) -> viewModel.showToast("fill Password field!!")

            else -> {
                val user = User(user_name,email, password,"offline", user_name.toLowerCase(Locale.ROOT))
                viewModel.signUp(user)
            }
        }
    }

    private fun moveToHomeActivity() {
        val i = Intent(activity, HomeActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        activity?.finish()
    }
}
