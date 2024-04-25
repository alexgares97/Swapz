package com.eug.swapz.datasources


import com.eug.swapz.datasources.interfaces.IMainDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
}