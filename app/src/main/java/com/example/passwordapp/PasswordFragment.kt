package com.example.passwordapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordapp.adapter.PasswordAdapter
import com.example.passwordapp.data.PasswordApplication
import com.example.passwordapp.databinding.FragmentPasswordBinding
import com.example.passwordapp.viewmodel.PasswordViewModel
import com.example.passwordapp.viewmodel.PasswordViewModelFactory


class PasswordFragment : Fragment() {

    /**
     * Get view model instance
     */
    private val viewModel: PasswordViewModel by activityViewModels {
        PasswordViewModelFactory(
            (activity?.application as PasswordApplication).database.passwordDao()
        )
    }

    private lateinit var binding: FragmentPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creates adapter: Item onClicked in recyclerview.
        // Navigates to adding screen. Passes password's id and website name.
        val adapter = PasswordAdapter { password ->
            val action =
                PasswordFragmentDirections.actionPasswordFragmentToAddPasswordFragment(
                    title = "Edit account",
                    passwordId = password.id)
            findNavController().navigate(action)
        }

        binding.apply {
            recyclerView.adapter = adapter
            lifecycleOwner = viewLifecycleOwner
            passwordFragment = this@PasswordFragment
        }

        // Add observer on password data. If data changes, update adapter data
        viewModel.allPasswords.observe(viewLifecycleOwner) { passwords ->
            passwords.let {
                adapter.submitList(passwords)
            }
        }
    }

    fun goToAddPasswordFragment() {
        val action =
            PasswordFragmentDirections.actionPasswordFragmentToAddPasswordFragment(
                title = "Add a new account"
            )
        findNavController().navigate(action)
    }
}