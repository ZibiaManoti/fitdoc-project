package com.example.fitdocv2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitdocv2.ui.theme.FitDocTheme
import com.example.fitdocv2.utils.GoogleSignInHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val googleSignInHelper by lazy { GoogleSignInHelper(this) }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            googleSignInHelper.handleSignInResult(
                data = result.data,
                onSuccess = { navigateToMainApp() },
                onFailure = { exception ->
                    showError(exception.message ?: "Google Sign-In failed")
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        setContent {
            FitDocTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(
                        onLoginSuccess = { navigateToMainApp() },
                        onRegisterSuccess = { navigateToMainApp() },
                        googleSignInHelper = googleSignInHelper // Pass it here
                    )
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        googleSignInHelper.startSignIn(
            onSuccess = { intentSenderRequest ->
                googleSignInLauncher.launch(intentSenderRequest)
            },
            onFailure = { exception ->
                showError(exception.message ?: "Google Sign-In failed")
            }
        )
    }

    private fun navigateToMainApp() {
        // Navigate to the main app screen (e.g., HomeScreen)
        // You can use a navigation library or simply update the UI state
    }

    private fun showError(message: String) {
        // Show error message (e.g., using a Snackbar or Toast)
    }
}

@Composable
fun AppContent(
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    googleSignInHelper: GoogleSignInHelper // Add this parameter
) {
    val navController = rememberNavController()
    val isLoggedIn = remember { mutableStateOf(false) }

    if (isLoggedIn.value) {
        MainApp(navController = navController)
    } else {
        AuthScreen(
            onLoginSuccess = { isLoggedIn.value = true },
            onRegisterSuccess = { isLoggedIn.value = true },
            googleSignInHelper = googleSignInHelper // Pass it here
        )
    }
}

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    googleSignInHelper: GoogleSignInHelper // Add this parameter
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = { navController.navigate("register") },
                googleSignInHelper = googleSignInHelper // Pass it here
            )
        }
        composable("register") {
            RegistrationScreen(
                onRegisterSuccess = onRegisterSuccess,
                onNavigateToLogin = { navController.navigate("login") },
                googleSignInHelper = googleSignInHelper // Pass it here
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    googleSignInHelper: GoogleSignInHelper // Add this parameter
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isFormValid =
            email.isNotBlank() &&
            password.isNotBlank()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                googleSignInHelper.handleSignInResult(
                    data = result.data,
                    onSuccess = onLoginSuccess,
                    onFailure = { exception ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Google Sign-In failed: ${exception.message}")
                        }
                    }
                )
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Google Sign-In canceled or failed")
                }
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Login failed: ${task.exception?.message}")
                            }
                        }
                    }
            }, enabled = isFormValid) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text(text = "Don't have an account? Register")
            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = {
//                googleSignInHelper.startSignIn(
//                    onSuccess = { intentSenderRequest ->
//                        launcher.launch(intentSenderRequest)
//                    },
//                    onFailure = { exception ->
//                        scope.launch {
//                            snackbarHostState.showSnackbar("Google Sign-In failed: ${exception.message}")
//                        }
//                    }
//                )
//            }) {
//                Text(text = "Sign in with Google")
//            }
        }
    }
}

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    googleSignInHelper: GoogleSignInHelper // Add this parameter
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isPasswordValid = password.length >= 6
    val doPasswordsMatch = password == confirmPassword

    val isFormValid = name.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword &&
            password.length >= 6

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                googleSignInHelper.handleSignInResult(
                    data = result.data,
                    onSuccess = onRegisterSuccess,
                    onFailure = { exception ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Google Sign-In failed: ${exception.message}")
                        }
                    }
                )
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Google Sign-In canceled or failed")
                }
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = password.isNotBlank() && !isPasswordValid,
                supportingText = {
                    if (password.isNotBlank() && !isPasswordValid) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPassword.isNotBlank() && !doPasswordsMatch,
                supportingText = {
                    if (confirmPassword.isNotBlank() && !doPasswordsMatch) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Reset error message
                errorMessage = null

                // Validate fields
                when {
                    name.isBlank() -> errorMessage = "Name cannot be empty"
                    email.isBlank() -> errorMessage = "Email cannot be empty"
                    password.isBlank() -> errorMessage = "Password cannot be empty"
                    confirmPassword.isBlank() -> errorMessage = "Confirm Password cannot be empty"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    else -> {
                        // If all validations pass, proceed with registration
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onRegisterSuccess()
                                } else {
                                    errorMessage = task.exception?.message ?: "Registration failed"
                                }
                            }
                    }
                }
            }, enabled = isFormValid) {
                Text(text = "Register")
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text(text = "Have an account? Login")
            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = {
//                googleSignInHelper.startSignIn(
//                    onSuccess = { intentSenderRequest ->
//                        launcher.launch(intentSenderRequest)
//                    },
//                    onFailure = { exception ->
//                        scope.launch {
//                            snackbarHostState.showSnackbar("Google Sign-In failed: ${exception.message}")
//                        }
//                    }
//                )
//            }) {
//                Text(text = "Sign in with Google")
//            }
        }
    }
}

@Composable
fun MainApp(navController: NavHostController) { // Use NavHostController instead of NavController
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen() }
                composable("profile") { ProfileScreen() }
                composable("insights") { InsightsScreen() }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var steps by remember { mutableStateOf(5000) }
    var heartRate by remember { mutableStateOf(75) }
    var caloriesBurned by remember { mutableStateOf(300) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Steps Card
        FitnessMetricCard(
            title = "Steps",
            value = steps.toString(),
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            onClick = { steps += 1000 }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Heart Rate Card
        FitnessMetricCard(
            title = "Heart Rate",
            value = "$heartRate bpm",
            icon = Icons.Default.Favorite,
            onClick = { heartRate = (60..100).random() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calories Burned Card
        FitnessMetricCard(
            title = "Calories Burned",
            value = "$caloriesBurned kcal",
            icon = Icons.Default.LocalFireDepartment,
            onClick = { caloriesBurned += 50 }
        )
    }
}

@Composable
fun FitnessMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Name: John Doe",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Age: 30",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Weight: 70 kg",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun InsightsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Weekly Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Steps: 35,000",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Calories Burned: 2,100 kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Active Days: 5/7",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Profile", "profile", Icons.Default.Person),
        BottomNavItem("Insights", "insights", painterResource(id = R.drawable.ic_insights))
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.icon is ImageVector) {
                        Icon(imageVector = item.icon, contentDescription = item.title)
                    } else {
                        Icon(painter = item.icon as Painter, contentDescription = item.title)
                    }
                },
                label = { Text(text = item.title) },
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary, // Highlight color
                    selectedTextColor = MaterialTheme.colorScheme.primary, // Highlight color
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Dimmed color
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Dimmed color
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    FitDocTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    FitDocTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInsightsScreen() {
    FitDocTheme {
        InsightsScreen()
    }
}

class MockGoogleSignInHelper(context: Context) : GoogleSignInHelper(context) {
    override fun startSignIn(onSuccess: (IntentSenderRequest) -> Unit, onFailure: (Exception) -> Unit) {
        // Mock implementation, does nothing
    }

    override fun handleSignInResult(data: Intent?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Mock implementation, does nothing
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    FitDocTheme {
        val context = LocalContext.current
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToRegister = {},
            googleSignInHelper = MockGoogleSignInHelper(context)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistrationScreen() {
    FitDocTheme {
        val context = LocalContext.current
        RegistrationScreen(onRegisterSuccess = {}, onNavigateToLogin = {}, googleSignInHelper = MockGoogleSignInHelper(context))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBottomNavigationBar() {
    FitDocTheme {
        val navController = rememberNavController()
        BottomNavigationBar(navController = navController)
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: Any)