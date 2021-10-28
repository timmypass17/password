package com.example.passwordapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.passwordapp.AddPasswordFragment
import com.example.passwordapp.data.PasswordApplication
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.databinding.ItemPasswordBinding
import com.example.passwordapp.viewmodel.PasswordViewModel
import com.example.passwordapp.viewmodel.PasswordViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PasswordAdapter(private val onItemClicked: (Password) -> Unit) :
    ListAdapter<Password, PasswordAdapter.PasswordViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        return PasswordViewHolder(ItemPasswordBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val password = getItem(position)
        // Could put this inside bind()
        holder.itemView.setOnClickListener {
            onItemClicked(password)
        }
        holder.bind(password)
    }

    class PasswordViewHolder(private var binding: ItemPasswordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pass: Password) {
            binding.apply {
                if (pass.imgUrl.isNotBlank()) {
                    val imgUri = (pass.imgUrl).toUri().buildUpon().scheme("https").build()
                    ivWebsiteIcon.load(imgUri)
                }
                tvWebsite.text = pass.websiteName
                tvUsername.text = pass.username
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Password>() {
            override fun areItemsTheSame(oldPassword: Password, newPassword: Password): Boolean {
                return oldPassword === newPassword
            }

            // TODO: not sure this works, duplicates are still working
            override fun areContentsTheSame(oldPassword: Password, newPassword: Password): Boolean {
                // If password are from same website and have same username, they are the same account
                if (oldPassword.websiteName == newPassword.websiteName &&
                    oldPassword.username == newPassword.username) {
                    return true
                }

                return false
            }
        }
    }

}