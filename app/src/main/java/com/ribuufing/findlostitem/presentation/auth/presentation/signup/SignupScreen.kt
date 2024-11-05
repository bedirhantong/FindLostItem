package com.ribuufing.findlostitem.presentation.auth.presentation.signup

import android.annotation.SuppressLint
import android.widget.Toast
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ribuufing.findlostitem.R
import com.ribuufing.findlostitem.presentation.auth.presentation.AuthViewModel
import com.ribuufing.findlostitem.utils.Result

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val userState by authViewModel.userState.observeAsState()
    val scaffoldState = rememberScaffoldState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val imageUrl = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val storageRef = Firebase.storage
            .getReference("auth/")

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    listResult.items[0].downloadUrl.addOnSuccessListener { uri ->
                        imageUrl.value = uri.toString()
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
                // Kayıt başarılı olduğunda kullanıcıyı giriş ekranına yönlendir
                Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true } // Register ekranını yığınlardan kaldırarak geri dönüldüğünde login ekranında kalır.
                }
            }
            is Result.Failure -> {
                val errorMessage = (userState as? Result.Failure)?.exception?.localizedMessage
                Toast.makeText(context, "Register failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                // Loading state
            }
            else -> Unit
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { scaffoldState.snackbarHostState }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            // Background Image from Firebase with Full Width and Progress Indicator
            Box(
                modifier = Modifier
                    .height(350.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFED822B)
                    )
                } else {
                    AsyncImage(
                        model = imageUrl.value ?: R.drawable.no_intrenet,
                        contentDescription = "Background",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Title
            Text(
                text = "Create an Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            // Name Input
            OutlinedTextField(
                value = name,
                maxLines = 1,
                onValueChange = { name = it },
                label = { Text("Name") },
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
                )
            )

            // Email Input
            OutlinedTextField(
                value = email,
                maxLines = 1,
                onValueChange = { email = it },
                label = { Text("Email") },
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

            // Signup Button
            Button(
                onClick = {
                    authViewModel.registerUser(email, password, name)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFED822B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Signup", color = Color.White)
            }

            // Login Prompt
            Text(
                text = "Already have an account? Log in.",
                color = Color(0xFFED822B),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { navController.navigate("login") }
            )
        }
    }
}