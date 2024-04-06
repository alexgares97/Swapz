package com.eug.swapz.datasources


import android.content.ContentValues.TAG
import android.util.Log
import com.eug.swapz.datasources.interfaces.ISessionDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

import kotlinx.coroutines.tasks.await

/**
 * A data source for Firebase Authentication. This class provides methods for logging in,
 * signing up, and logging out users, as well as checking the current authentication state.
 */
class SessionDataSource : ISessionDataSource{
    // Initialize Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    override fun getCurrentUser(): FirebaseUser? {
        // Get the currently authenticated user from the Firebase Authentication SDK
        Log.d(TAG, "OK!!")

        return auth.currentUser
    }
    fun getCurrentUserId(): String? {
        // Get the currently authenticated user from Firebase Authentication
        val currentUser = auth.currentUser

        // If the current user is not null, return their user ID
        return currentUser?.uid
    }

    override fun isLoggedIn(): Boolean {
        // Check if the current user is null (i.e., no user is logged in)
        // If the current user is not null, then a user is logged in
        return this.getCurrentUser() != null

    }

    override suspend fun loginUserAnonymous(): Boolean {
        this.signOutUser()
        return try {
            // Sign in the user anonymously using the Firebase Authentication SDK
            val result = auth.signInAnonymously().await()
            // Return true if the login was successful (i.e., the user is not null)
            result.user != null
        } catch (e: Exception) {
            // If an exception occurs, return false to indicate that the login was not successful
            false
        }
    }

    override suspend fun loginUser(email: String, password: String): Boolean {
        this.signOutUser()
        return try {
            // Sign in the user with the specified email and password using the
            // Firebase Authentication SDK
            val result = auth.signInWithEmailAndPassword(email, password).await()
            // Return true if the login was successful (i.e., the user is not null)
            result.user != null
        } catch (e: Exception) {
            // If an exception occurs, return false to indicate that the login was not successful
            false
        }
    }

    override suspend fun signUpUser(email: String, password: String, username: String, name: String): Boolean {
        this.signOutUser()
        return try {
            // Create a new user account with the specified email and password using the
            // Firebase Authentication SDK
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // Update the user's profile with the provided username
            result.user?.let { user ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            // Return true if the signup was successful (i.e., the user is not null)
            result.user != null
        } catch (e: Exception) {
            // If an exception occurs, return false to indicate that the signup was not successful
            false
        }
    }


    override fun signOutUser() {
        // Sign out the currently authenticated user using the Firebase Authentication SDK
        auth.signOut()
    }
}