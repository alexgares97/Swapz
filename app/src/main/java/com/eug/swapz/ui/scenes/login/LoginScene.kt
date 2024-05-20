@file:OptIn(ExperimentalMaterial3Api::class)

package com.eug.swapz.ui.scenes.login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.eug.swapz.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.ExperimentalCoroutinesApi

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScene(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf(TextFieldValue("pepeluis@gmail.com")) }
    val passwordState = remember { mutableStateOf(TextFieldValue("admin123")) }

    fun validateInputs(callback: (email: String, password: String) -> Unit) {
        val email = emailState.value.text
        val password = passwordState.value.text
        if (email.isNotEmpty() && password.isNotEmpty()) {
            callback(email, password)
        } else {
            Toast.makeText(
                context,
                "Porfavor introduce email y contraseña.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1D1D1D),
                        Color(0xFF0D0D0D)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(R.drawable.logo_sombra), // Use your actual logo resource here
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage.value,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF2F96D8),
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small
                )

                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2F96D8),
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFF2F96D8),
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            validateInputs { email, password ->
                                viewModel.login(email, password)
                            }
                        },
                        enabled = !viewModel.isLoading.value,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp), // Adjust padding between buttons
                        shape = RoundedCornerShape(50) // More rounded corners
                    ) {
                        Text(text = "Login", color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.navigateToRegister()
                        },
                        enabled = !viewModel.isLoading.value,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F96D8)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp), // Adjust padding between buttons
                        shape = RoundedCornerShape(50) // More rounded corners
                    ) {
                        Text(text = "Register", color = Color.White)
                    }
                }

                Text(
                    text = "Forgot Password?",
                    color = Color(0xFF2F96D8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            // Handle forgot password click
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun LoginScenePreview() {
    SwapzTheme {
        LoginFactory(
            navController = rememberNavController(),
            sessionDataSource = SessionDataSource()
        )
    }
}
