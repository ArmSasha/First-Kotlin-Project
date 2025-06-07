data class RedditResponse(val data: RedditData)
data class RedditData(val children: List<RedditChild>)
data class RedditChild(val data: RedditPost)
data class RedditPost(
    val title: String,
    val author: String,
    val created_utc: Long,
    val ups: Int,
    val downs: Int,
    val thumbnail: String,
    val selftext: String,
    val url: String,
    val permalink: String
)