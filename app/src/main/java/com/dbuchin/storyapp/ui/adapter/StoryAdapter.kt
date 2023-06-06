package com.dbuchin.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dbuchin.storyapp.R
import com.dbuchin.storyapp.data.remote.response.StoryItems
import com.dbuchin.storyapp.databinding.ItemStoryBinding
import com.dbuchin.storyapp.ui.detail.DetailActivity

class StoryAdapter(
    private val listStory: List<StoryItems>
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStoryBinding.bind(itemView)
        fun bindItem(item: StoryItems) {
            with(binding) {
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .into(photo)

                name.text = item.name
                description.text = item.description

                itemView.setOnClickListener {
                    DetailActivity.start(
                        itemView.context,
                        item.photoUrl as String,
                        item.id as String,
                        Pair(photo, "ivItemPhoto")
                    )

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}