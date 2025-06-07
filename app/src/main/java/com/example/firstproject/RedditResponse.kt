package com.example.firstproject

data class RedditResponse(
    val data: RedditData
)

data class RedditData(
    val children: List<RedditPostWrapper>,
    val after: String?
)

data class RedditPostWrapper(
    val data: RedditPostData
)
