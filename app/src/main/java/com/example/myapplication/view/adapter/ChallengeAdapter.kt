package com.example.myapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Challenge
import com.example.myapplication.utils.AnimationUtils

class ChallengeAdapter(
    private var challenges: MutableList<Challenge>,
    private val onEditClick: (Challenge) -> Unit,
    private val onDeleteClick: (Challenge) -> Unit
) : RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtDescription =
            itemView.findViewById<TextView>(
                R.id.txtDescription
            )
        val btnEdit =
            itemView.findViewById<ImageButton>(
                R.id.btnEdit
            )
        val btnDelete =
            itemView.findViewById<ImageButton>(
                R.id.btnDelete
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_challenge,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val challenge = challenges[position]

        holder.txtDescription.text =
            challenges[position].description

        holder.btnEdit.setOnClickListener {
            AnimationUtils.pressAnimation(holder.btnEdit){
                onEditClick(challenge)
            }
        }
        holder.btnDelete.setOnClickListener {
            AnimationUtils.pressAnimation(holder.btnDelete){
                onDeleteClick(challenge)
            }
        }
    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    fun updateList(newList: MutableList<Challenge>) {
        challenges = newList
        notifyDataSetChanged()
    }
}