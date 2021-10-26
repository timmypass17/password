package com.example.passwordapp

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.passwordapp.data.PasswordApplication
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.databinding.FragmentAddPasswordBinding
import com.example.passwordapp.viewmodel.PasswordViewModel
import com.example.passwordapp.viewmodel.PasswordViewModelFactory


/**
 * Fragment to add or update an item in the Password database.
 */
class AddPasswordFragment : Fragment() {

    /**
     * Instantiate view model using PasswordViewModelFactory.
     *  Use [activityViewModels] to share view model across fragment.
     */
    private val viewModel: PasswordViewModel by activityViewModels {
        PasswordViewModelFactory(
            (activity?.application as PasswordApplication).database.passwordDao()
        )
    }
    private val navigationArgs: AddPasswordFragmentArgs by navArgs()
    lateinit var password: Password
    private var passwordId = -1

    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // returns -1 (default value), or Password id
        passwordId = navigationArgs.passwordId
        viewModel.retrievePassword(passwordId).observe(viewLifecycleOwner) { selectedPassword ->
            if (passwordId != -1) {
                password = selectedPassword
                bind(selectedPassword)
            }
        }
        binding.apply {
            // Save button onClick to save password
            btnSave.setOnClickListener {
                updatePassword()
            }
            // Cancel button to navigate back to PasswordFragment
            btnCancel.setOnClickListener {
                cancelPasswordCreation()
            }
            // Delete button onClick
            btnDelete.setOnClickListener {
                deletePassword()
            }
        }
    }

    /**
     * Add new password or Updates existing password
     */
    private fun updatePassword() {
        // Check for valid password input
        if (isEntryValid()) {
            // 1. Are we adding?
            if (passwordId == -1) {
                viewModel.addNewPassword(
                    binding.tvWebsite.text.toString(),
                    binding.tvUsername.text.toString(),
                    binding.tvPassword.text.toString()
                )
            }
            // 2. Are we updating?
            else {
                viewModel.updatePassword(
                    passwordId,
                    binding.tvWebsite.text.toString(),
                    binding.tvUsername.text.toString(),
                    binding.tvPassword.text.toString()
                )
            }
        }
        // Navigate back to password fragment
        val action = AddPasswordFragmentDirections.actionAddPasswordFragmentToPasswordFragment()
        findNavController().navigate(action)
    }

    /**
     * Delete password
     */
    private fun deletePassword() {
        viewModel.deleteItem(password)
        findNavController().navigateUp()
    }

    /**
     * Prepopulate text views with this password's information
     */
    private fun bind(password: Password) {
        binding.apply {
            tvWebsite.setText(password.websiteName)
            tvUsername.setText(password.username)
            tvPassword.setText(password.password)
        }
    }

    private fun cancelPasswordCreation() {
        findNavController().navigate(R.id.action_addPasswordFragment_to_passwordFragment)
    }

    /**
     *  Check if user has valid input
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.tvWebsite.text.toString(),
            binding.tvUsername.text.toString(),
            binding.tvPassword.text.toString()
        )
    }

}