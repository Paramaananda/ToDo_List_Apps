package com.example.todo_list_apps.Pages


import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todo_list_apps.AuthViewModel
import com.example.todo_list_apps.Authstate
import com.example.todo_list_apps.R
import com.example.todo_list_apps.ui.theme.urbanistFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigninPage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,  // Opsional untuk preview
    authViewModel: AuthViewModel? = null
){
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }

    val authState = authViewModel?.authState?.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState?.value) {
        when (authState?.value){
            is Authstate.Authenticated -> navController?.navigate("home")
            is Authstate.Error -> Toast.makeText(
                context,
                (authState.value as Authstate.Error).message,
                Toast.LENGTH_LONG
            ).show()
            else -> Unit
        }
    }

    Box(modifier = Modifier
        .background(Color.White)){
        Image(
            painter = painterResource(id = R.drawable.blob),
            contentDescription = null,
            contentScale = ContentScale.FillWidth, // Crop agar memenuhi area horizontal
            modifier = Modifier
                .fillMaxWidth() // Melebar dari ujung kiri ke kanan
                .height(118.dp) // Sesuaikan tinggi gambar
        )

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                text = "TaskFlow",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp),
                text = "Sign In",
                color = Color.Black,
                fontSize = 32.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Start),
                text = "Welcome back! Please sign in to your account to continue enjoying the features.",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp), // Jarak atas dan bawah garis
                thickness = 1.dp,
                color = Color.Gray
            )

            //email
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp),
                text = "Email Address",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
            )

            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00A3FF),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            //Password
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp),
                text = "Password",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = urbanistFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
            )

            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00A3FF),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Menyesuaikan tinggi
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A3FF), // Warna latar belakang (ungu)
                ),
                onClick = { authViewModel?.signin(email, password) }
            ) {
                Text(
                    text = "Sign In", // Teks tombol
                    color = Color.White, // Warna teks
                    fontSize = 16.sp, // Ukuran font
                    fontWeight = FontWeight.Bold // Gaya huruf
                )
            }

            ClickableText(
                text = buildAnnotatedString {
                    append("Don’t have an account? ")
                    pushStringAnnotation(tag = "REGISTER", annotation = "register")
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue, // Warna teks Register
                            fontWeight = FontWeight.Bold // Teks Register tebal
                        )
                    ) {
                        append("Register")
                    }
                    pop()
                },
                onClick = { offset ->
                    // Periksa apakah bagian Register yang diklik
                    buildAnnotatedString {
                        append("Don’t have an account? ")
                        pushStringAnnotation(tag = "REGISTER", annotation = "register")
                        append("Register")
                        pop()
                    }.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                        .firstOrNull()?.let {
                            // Aksi saat "Register" diklik
                            println("Register clicked!")
                            navController?.navigate("register")
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SigninPagePreview(){
    SigninPage()
}


