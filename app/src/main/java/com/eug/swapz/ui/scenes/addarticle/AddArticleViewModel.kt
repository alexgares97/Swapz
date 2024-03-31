package com.eug.swapz.ui.scenes.addarticle

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class AddArticleViewModel (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,

    ) : ViewModel() {


    // Function to update the image URI
    fun signOut() {
        viewModelScope.launch {
            sessionDataSource.signOutUser()
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.MAIN.value) {
                    inclusive = true
                }
            }
        }
    }

    fun home() {
        navController.popBackStack()
    }
    fun navigateToMain() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.MAIN.value)
        }
    }
    /*private fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }*/


    fun addArticle(title: String, desc: String, status: String, cat: String, value: Int?, img: List<String>)
    {
        val articleRef = FirebaseDatabase.getInstance().getReference("articles")

        // Get a reference to the articles node
        val articlesQuery = articleRef.orderByKey().limitToLast(1)

        articlesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the key of the last article
                val lastArticleKey = dataSnapshot.children.firstOrNull()?.key
                // Increment the numeric part of the last article key and generate the new key
                val newArticleKey = getNextArticleKey(lastArticleKey)
                // Get a reference to the new article location
                val newArticleRef = articleRef.child(newArticleKey)
                // Set the values for the new article
                newArticleRef.child("title").setValue(title)
                newArticleRef.child("desc").setValue(desc)
                newArticleRef.child("status").setValue(status)
                newArticleRef.child("cat").setValue(cat)
                newArticleRef.child("value").setValue(value)

                // Add each image URL to the "carrusel" node with incremental indices
                val carruselRef = newArticleRef.child("carrusel")
                img.forEachIndexed { index, imageUrl ->
                    carruselRef.child(index.toString()).setValue(imageUrl)
                }

                // value?.let { newArticleRef.child("value").setValue(it) } // Only set if value is not null
                // Article added successfully
                Log.d(ContentValues.TAG, "Article added successfully")
                // You can perform any necessary actions here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error occurred while retrieving the last article
                Log.e(ContentValues.TAG, "Error retrieving last article", databaseError.toException())
                // You can handle the error appropriately, e.g., show an error message to the user
            }
        })

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