package com.mouritech.crashnotifier.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.data.model.EmergencyContacts

class ContactDetails(private val mList: List<EmergencyContacts>) : RecyclerView.Adapter<ContactDetails.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val mobileNumber: TextView = itemView.findViewById(R.id.number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_list_data, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.name.text  = ItemsViewModel.name
        holder.mobileNumber.text = ItemsViewModel.mobile
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}