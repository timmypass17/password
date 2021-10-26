package com.example.passwordapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val viewModel: PasswordViewModel by activityViewModels {
        PasswordViewModelFactory(
            (activity?.application as PasswordApplication).database.passwordDao()
        )
    }
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Item clicked in recyclerview
        val adapter = PasswordAdapter{ password ->
            val action = PasswordFragmentDirections.actionPasswordFragmentToAddPasswordFragment(password.id)
            findNavController().navigate(action)
        }
        binding.apply {
            recyclerView.adapter = adapter
            // Fab onClick to navigate to add password fragment
            floatingActionButton.setOnClickListener {
                val action = PasswordFragmentDirections.actionPasswordFragmentToAddPasswordFragment()
                findNavController().navigate(action)
            }
        }

        viewModel.allPasswords.observe(viewLifecycleOwner) { passwords ->
            passwords.let {
                adapter.submitList(passwords)
            }
        }

    }
}