package com.example.firstproject

data class RedditPostData(
    val title: String,
    val selftext: String,
    val author: String,
    val created_utc: Long,
    val ups: Int,
    val downs: Int,
    val thumbnail: String,
    val url: String,
    val permalink: String
)
