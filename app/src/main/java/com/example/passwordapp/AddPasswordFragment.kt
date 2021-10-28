package com.example.passwordapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.passwordapp.data.PasswordApplication
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.databinding.FragmentAddPasswordBinding
import com.example.passwordapp.viewmodel.PasswordViewModel
import com.example.passwordapp.viewmodel.PasswordViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


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
    var passwordId = -1 // // default value, didn't receive password

    private lateinit var binding: FragmentAddPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_password, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passwordId = navigationArgs.passwordId
        if (passwordId > 0) { // we received a password
            viewModel.retrievePassword(passwordId).observe(viewLifecycleOwner) { selectedPassword ->
                password = selectedPassword
                binding.currentPassword = selectedPassword // set data binding currentPassword

                if (selectedPassword.imgUrl.isNotBlank()) {
                    val imgUri = (password.imgUrl).toUri().buildUpon().scheme("https").build()
                    binding.ivWebsiteIcon.load(imgUri)
                }
            }
        }

        binding.apply {
            passwordViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            addPasswordFragment = this@AddPasswordFragment
            // onclick listener in xml and data binding
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    /**
     * Add new password or updates existing password
     */
    fun updatePassword() {
        checkForEmptyInput()
        // Check for valid password input
        if (isEntryValid()) {
            // 1. Are we adding?
            if (passwordId == -1) {
                viewModel.addNewPassword(
                    binding.tvWebsite.text.toString(),
                    binding.tvUsername.text.toString(),
                    binding.tvPassword.text.toString(),
                )


                Snackbar.make(requireView(), "Added \"${binding.tvWebsite.text.toString()}\" to my account", Snackbar.LENGTH_LONG)
                    .setActionTextColor(resources.getColor(R.color.green_light))
                    .setAction("Dismiss") {
                        // Responds to click on the action
                    }
                    .show()
            }
            // 2. Are we updating?
            else {
                Snackbar.make(requireView(), "Updated \"${password.websiteName}\" entry", Snackbar.LENGTH_LONG)
                    .setActionTextColor(resources.getColor(R.color.green_light))
                    .setAction("Dismiss") {
                        // Responds to click on the action
                    }
                    .show()
                viewModel.updatePassword(
                    passwordId,
                    binding.tvWebsite.text.toString(),
                    binding.tvUsername.text.toString(),
                    binding.tvPassword.text.toString()
                )
            }
            // Navigate back to password fragment
            val action = AddPasswordFragmentDirections.actionAddPasswordFragmentToPasswordFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Displays error signs if text is empty
     */
    private fun checkForEmptyInput() {
        binding.apply {
            if (tvWebsite.text.toString().isBlank()) {
                websiteLabel.error = "*Required"
            } else {
                websiteLabel.error = null
            }
            if (tvUsername.text.toString().isBlank()) {
                usernameLabel.error = "*Required"
            } else {
                usernameLabel.error = null
            }
            if (tvPassword.text.toString().isBlank()) {
                passwordLabel.error = "*Required"
            } else {
                passwordLabel.error = null
            }
        }
    }

    /**
     * Delete password from room database
     */
    private fun deletePassword() {
        if (isEntryValid()) {
            viewModel.deleteItem(password)
            findNavController().navigateUp()
        }
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Are you sure you want to delete?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                Snackbar.make(requireView(), "Deleted \"${password.websiteName}\" entry", Snackbar.LENGTH_LONG)
                    .setActionTextColor(resources.getColor(R.color.green_light))
                    .setAction("Dismiss") {
                        // Responds to click on the action
                    }
                    .show()
                deletePassword()
            }
            .show()
    }

    /**
     * Navigate back to home page
     */
    fun cancelPasswordCreation() {
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

    /**
     * Generate random password
     */
    fun getRandomString(length: Int) : String {
        return (1..length)
            .map { getRandomChar() }
            .joinToString("")
    }

    private fun getRandomChar(): Char {
        val randomNum = (0..100).random()
        return when {
            (randomNum < 15) -> ('A'..'Z').random()
            (randomNum < 30) -> ('a'..'z').random()
            (randomNum < 70) -> ('0'..'9').random()
            else -> ("~`!@ #$%^&*()_-+={[}]|\\:;\"'<,>.?/").random()
        }
    }

}