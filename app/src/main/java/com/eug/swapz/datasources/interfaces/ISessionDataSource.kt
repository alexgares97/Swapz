package com.eug.swapz.datasources.interfaces


import android.location.Location
import com.google.firebase.auth.FirebaseUser

interface ISessionDataSource {
    /**
     * Returns the currently authenticated Firebase user, or null if no user is authenticated.
     *
     * @return The currently authenticated Firebase user, or null if no user is authenticated.
     */
    fun getCurrentUser(): FirebaseUser?

    /**
     * Returns a boolean indicating whether a user is currently logged in or not.
     *
     * @return True if a user is logged in, false otherwise.
     */
    fun isLoggedIn(): Boolean

    /**
     * Logs in the user anonymously using Firebase Authentication.
     *
     * @return True if the login was successful, false otherwise.
     */
    suspend fun loginUserAnonymous(): Boolean

    /**
     * Logs in the user with the specified email and password using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     *
     * @return True if the login was successful, false otherwise.
     */
    suspend fun loginUser(email: String, password: String): Boolean

    /**
     * Signs up the user with the specified email and password using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     *
     * @return True if the signup was successful, false otherwise.
     */
    suspend fun signUpUser(email: String, password: String, username: String, name: String, photo: String): Boolean

    /**
     * Signs out the currently authenticated user using Firebase Authentication.
     */
    fun signOutUser()
}