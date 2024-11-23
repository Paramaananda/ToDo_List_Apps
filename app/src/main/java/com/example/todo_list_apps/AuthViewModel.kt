package com.example.todo_list_apps


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = Authstate.Unauthenticated
        } else {
            _authState.value = Authstate.Authenticated(currentUser.email)
        }
    }

    fun signin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = Authstate.Error("Email or Password can't be empty")
            return
        }
        _authState.value = Authstate.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = Authstate.Authenticated(auth.currentUser?.email)
                } else {
                    _authState.value = Authstate.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value =
                Authstate.Error("Email, Password, or Confirm Password can't be empty")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = Authstate.Error("Invalid email format")
            return
        }
        if (password.length < 6) {
            _authState.value = Authstate.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authState.value = Authstate.Error("Passwords do not match")
            return
        }

        _authState.value = Authstate.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = Authstate.Authenticated(auth.currentUser?.email)
                    // Navigasi ke halaman home setelah signup berhasil
                    // Anda dapat menggunakan navController jika tersedia
                } else {
                    _authState.value = Authstate.Error(
                        task.exception?.message ?: "Something went wrong, please try again"
                    )
                }
            }
    }
    fun signout() {
        auth.signOut()
        _authState.value = Authstate.Unauthenticated
    }


}

sealed class Authstate {
    data class Authenticated(val email: String?) : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}


data class User(
    val displayName: String,
    val photoUrl: String?,
    val email: String
)