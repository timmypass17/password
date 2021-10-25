package com.example.passwordapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
    lateinit var password: Password

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

    /**
     * Add new password
     */
    private fun addNewItem() {
        // Check for valid password input
        if (isEntryValid()) {
            viewModel.addNewPassword(
                binding.tvWebsite.text.toString(),
                binding.tvUsername.text.toString(),
                binding.tvPassword.text.toString()
            )
        }
        // Navigate back to password fragment
        val action = AddPasswordFragmentDirections.actionAddPasswordFragmentToPasswordFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Save button onClick to save password
            btnSave.setOnClickListener {
                addNewItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}