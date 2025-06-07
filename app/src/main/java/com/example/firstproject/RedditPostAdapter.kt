package com.example.firstproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RedditPostAdapter(
    private val onPostClick: (String) -> Unit
) : RecyclerView.Adapter<RedditPostAdapter.PostViewHolder>() {

    private val posts = mutableListOf<RedditPostData>()

    fun addPosts(newPosts: List<RedditPostData>) {
        val start = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(start, newPosts.size)
    }

    fun clearPosts() {
        posts.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.titleTextView)
        private val description = view.findViewById<TextView>(R.id.descriptionTextView)
        private val author = view.findViewById<TextView>(R.id.authorTextView)
        private val date = view.findViewById<TextView>(R.id.dateTextView)
        private val image = view.findViewById<ImageView>(R.id.thumbnailImageView)
        private val ups = view.findViewById<TextView>(R.id.upsTextView)

        fun bind(post: RedditPostData) {
            title.text = post.title
            description.text = post.selftext.takeIf { it.isNotBlank() } ?: "[без описания]"
            author.text = "Автор: ${post.author}"
            date.text = "Дата: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(post.created_utc * 1000)}"
            ups.text = "Апвоуты: ${post.ups}"

            Glide.with(itemView.context)
                .load(if (post.thumbnail.startsWith("http")) post.thumbnail else null)
                .placeholder(R.drawable.placeholder) // добавь в res/drawable картинку-заглушку
                .into(image)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", "https://reddit.com${post.permalink}")
                context.startActivity(intent)
            }
        }
    }
}
