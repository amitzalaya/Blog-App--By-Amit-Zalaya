package com.example.readblogapp.blogmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readblogapp.R
import com.example.readblogapp.databinding.UserblogSimpleLayoutBinding
class UserBlogAdapter(
    private val context:Context,
    private var bloglist: MutableList<BlogModel>,
    private val itemClickListener: OnItemClickListener
):RecyclerView.Adapter<UserBlogAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(blogitems: BlogModel)
        fun onReadmore(blogitems: BlogModel)
        fun onDelete(blogitems: BlogModel)
    }


    inner class BlogViewHolder(private val binding: UserblogSimpleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogitems: BlogModel) {

            binding.heading.text = blogitems.heading
            binding.descrption.text = blogitems.post
            binding.date.text = blogitems.date
            binding.userName.text = blogitems.userName

            Glide.with(binding.userProfile.context)
                .load(blogitems.profile_image)
                .placeholder(R.drawable.userimage)
                .centerCrop()
                .into(binding.userProfile)


            // read button click listener
            binding.readmore.setOnClickListener {
                itemClickListener.onReadmore(blogitems)
            }
            // Delete button click listener
            binding.deleteblog.setOnClickListener {
                itemClickListener.onDelete(blogitems)
            }
            // Edit button click listener
            binding.editblog.setOnClickListener {
                itemClickListener.onEdit(blogitems)
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = UserblogSimpleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bloglist.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogitems = bloglist[position]
        holder.bind(blogitems)
    }

     fun saveData(blogsavedlist: ArrayList<BlogModel>) {
        this.bloglist = blogsavedlist
}

}