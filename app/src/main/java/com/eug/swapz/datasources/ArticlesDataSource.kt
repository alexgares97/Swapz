package com.eug.swapz.datasources


import android.content.ContentValues
import android.util.Log
import com.eug.swapz.datasources.interfaces.IMainDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ArticlesDataSource(private val database: FirebaseDatabase) : IMainDataSource {
    private var articles: List<Article> = mutableListOf()

    fun subscribe(callback: (List<Article>) -> Unit)  {
        val ref = database.getReference("articles")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetchedArticles = mutableListOf<Article>()

                for (articleSnapshot in snapshot.children) {
                    val article = articleSnapshot.getValue(Article::class.java)
                    if (article != null) {
                        article.id = articleSnapshot.key
                        fetchedArticles.add(article)
                    }
                }

                //Updating local copy
                articles = fetchedArticles
                callback(fetchedArticles)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // or callback with some default value
            }
        })
    }

    override suspend fun fetch(): List<Article> {
        return suspendCoroutine { continuation ->

            val ref = database.getReference("articles")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedArticles = mutableListOf<Article>()

                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Article::class.java)
                        if (article != null) {
                            article.id = articleSnapshot.key
                            fetchedArticles.add(article)
                        }
                    }

                    //Updating local copy
                    articles = fetchedArticles

                    continuation.resume(fetchedArticles)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
    suspend fun getUserArticles(userId: String): List<Article> {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles")
            ref.orderByChild("user").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedArticles = mutableListOf<Article>()

                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Article::class.java)
                        if (article != null) {
                            article.id = articleSnapshot.key
                            fetchedArticles.add(article)
                        }
                    }

                    continuation.resume(fetchedArticles)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
    suspend fun getCategoryArticles(category: String): List<Article> {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles")
            ref.orderByChild("cat").equalTo(category).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedArticles = mutableListOf<Article>()

                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Article::class.java)
                        if (article != null) {
                            article.id = articleSnapshot.key
                            fetchedArticles.add(article)
                        }
                    }

                    continuation.resume(fetchedArticles)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
    suspend fun deleteArticle(articleId: String) {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles").child(articleId)
            ref.removeValue()
                .addOnSuccessListener {
                    // Deletion successful
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    // Deletion failed
                    continuation.resumeWithException(exception)
                }
        }
    }
    suspend fun getUserIdFromCurrentArticle(articleId: String): String? {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles").child(articleId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val article = snapshot.getValue(Article::class.java)
                    val userId = article?.user
                    continuation.resume(userId)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }
            })
        }
    }




    override fun get(id: String): Article? {
        return articles.find { it.id == id }
    }
    suspend fun getArticleById(articleId: String): Article? {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles").child(articleId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val article = snapshot.getValue(Article::class.java)
                    if (article != null) {
                        article.id = snapshot.key
                        continuation.resume(article)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    suspend fun addArticle(article: Article) {
        try {
            // Get a reference to the articles node
            val articlesQuery = FirebaseDatabase.getInstance().getReference("articles").orderByKey().limitToLast(1)
            val dataSnapshot = articlesQuery.get().await()

            // Retrieve the key of the last article
            val lastArticleKey = dataSnapshot.children.firstOrNull()?.key

            // Increment the numeric part of the last article key and generate the new key
            val newArticleKey = getNextArticleKey(lastArticleKey)

            // Get a reference to the new article location
            val newArticleRef = FirebaseDatabase.getInstance().getReference("articles").child(newArticleKey)

            // Set the values for the new article
            newArticleRef.setValue(article).await()

            Log.d(ContentValues.TAG, "Article added successfully")
        } catch (e: Exception) {
            // Error occurred while adding the article
            Log.e(ContentValues.TAG, "Error adding article", e)
            // You can handle the error appropriately, e.g., show an error message to the user
        }
    }
    suspend fun updateArticle(article: Article) {
        return suspendCoroutine { continuation ->
            val ref = database.getReference("articles").child(article.id!!)
            ref.setValue(article)
                .addOnSuccessListener {
                    // Update successful
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    // Update failed
                    continuation.resumeWithException(exception)
                }
        }
    }

    private fun getNextArticleKey(lastArticleKey: String?): String {
        var nextKey = "art01" // Default key if there are no existing articles

        // If a last article key is available, extract the numeric part and increment it
        lastArticleKey?.let { key ->
            val numericPart = key.substring(3).toIntOrNull() ?: 0
            val nextNumericPart = numericPart + 1
            nextKey = "art${String.format("%02d", nextNumericPart)}"
        }
        return nextKey
    }
}