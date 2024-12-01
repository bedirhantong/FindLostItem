package com.ribuufing.findlostitem.presentation.auth.presentation.login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.ribuufing.findlostitem.navigation.BottomNavigationItems
import com.ribuufing.findlostitem.navigation.Routes
import com.ribuufing.findlostitem.presentation.auth.presentation.AuthViewModel
import com.ribuufing.findlostitem.utils.Result

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val userState by authViewModel.userState.observeAsState()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val imageUrl = remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val storageRef = Firebase.storage.getReference("auth/")
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    val loginImageRef = listResult.items.firstOrNull { it.name == "login.jpg" }
                    loginImageRef?.downloadUrl?.addOnSuccessListener {
                        imageUrl.value = it.toString()
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is Result.Success -> {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                navController.navigate(BottomNavigationItems.Home.route) {
                    popUpTo(Routes.Login.route) {
                        inclusive = true
                    }
                }
            }
            is Result.Failure -> {
                val errorMessage = (userState as? Result.Failure)?.exception?.localizedMessage
                Toast.makeText(context, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { scaffoldState.snackbarHostState }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(350.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFED822B)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl.value),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Mesaj
            Text(
                text = "You will never try to find your lost item alone.",
                fontSize = 16.sp,
                maxLines = 2,
                color = Color(0xFF99704D),
                modifier = Modifier.padding(16.dp)
            )

            // Email Input
            OutlinedTextField(
                value = email,
                maxLines = 1,
                onValueChange = { email = it },
                label = { Text("Email or username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFFF2EDE8),
                    focusedBorderColor = Color(0xFFED822B),
                    unfocusedBorderColor = Color(0x00686868),
                    focusedLabelColor = Color(0xFFED822B),
                    cursorColor = Color(0xFF99704D),
                    textColor = Color(0xFF99704D)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Password Input
            OutlinedTextField(
                value = password,
                maxLines = 1,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFFF2EDE8),
                    focusedBorderColor = Color(0xFFED822B),
                    unfocusedBorderColor = Color(0x00686868),
                    focusedLabelColor = Color(0xFFED822B),
                    cursorColor = Color(0xFF99704D),
                    textColor = Color(0xFF99704D)
                ),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Done else Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                }
            )

            // Forgot Password
            Text(
                text = "Forgot password?",
                color = Color(0xFF99704D),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )

            // Login Button
            Button(
                onClick = {
                    authViewModel.loginUser(email, password)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFED822B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Log in", color = Color.White)
            }

            // Sign Up Prompt
            Text(
                text = "Don’t have an account? Sign up",
                color = Color(0xFF99704D),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { navController.navigate(Routes.Signup.route) }
            )
        }
    }
}
