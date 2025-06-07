package com.example.firstproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    private var selectedSort: String = "relevance" // или "new" / "top" по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData = findViewById<EditText>(R.id.user_data)
        val button = findViewById<Button>(R.id.button)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RedditPostAdapter { postUrl ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(postUrl))
            startActivity(intent)
        }

        val sortTopCheckBox = findViewById<CheckBox>(R.id.sortTop)
        val sortNewCheckBox = findViewById<CheckBox>(R.id.sortNew)

        // Обработка переключений
        sortTopCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sortNewCheckBox.isChecked = false
                selectedSort = "top"
            } else if (!sortNewCheckBox.isChecked) {
                selectedSort = "relevance"
            }
        }

        sortNewCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sortTopCheckBox.isChecked = false
                selectedSort = "new"
            } else if (!sortTopCheckBox.isChecked) {
                selectedSort = "relevance"
            }
        }


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        var after: String? = null
        var isLoading = false

        fun loadRedditPosts(query: String, afterToken: String? = null) {
            if (isLoading) return
            isLoading = true

            lifecycleScope.launch {
                val url = buildString {
                    append("https://www.reddit.com/search.json?q=")
                    append(query)
                    append("&limit=20")
                    append("&sort=$selectedSort")  // 👈 добавили сортировку
                    if (afterToken != null) append("&after=$afterToken")
                }

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                val responseData = withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else null
                }

                responseData?.let {
                    val redditResponse = Gson().fromJson(it, RedditResponse::class.java)
                    val posts = redditResponse.data.children.map { it.data }
                    adapter.addPosts(posts)
                    after = redditResponse.data.after
                }

                isLoading = false
            }
        }

        button.setOnClickListener {
            val query = userData.text.toString().trim()
            if (query.isNotEmpty()) {
                adapter.clearPosts()
                after = null
                loadRedditPosts(query)
            }
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                if (lastVisible + 25 >= totalItemCount && after != null) {
                    val query = userData.text.toString().trim()
                    if (query.isNotEmpty()) loadRedditPosts(query, after)
                }
            }
        })
    }
}
