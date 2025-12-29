package com.example.akillikampusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.akillikampusapp.ui.auth.LoginScreen
import com.example.akillikampusapp.ui.home.HomeScreen
import com.example.akillikampusapp.ui.home.CreateNotificationScreen
import com.example.akillikampusapp.ui.home.NotificationListScreen
import com.example.akillikampusapp.ui.admin.AdminHomeScreen
import com.example.akillikampusapp.ui.admin.AdminUserListScreen
import com.example.akillikampusapp.ui.home.UserNotificationsScreen
import com.example.akillikampusapp.ui.map.MapScreen
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import com.example.akillikampusapp.data.model.Notification
import com.example.akillikampusapp.data.remote.NotificationService
import com.example.akillikampusapp.ui.auth.ForgetPasswordScreen

import androidx.compose.foundation.layout.*
import com.example.akillikampusapp.ui.map.MapPickerScreen
import com.example.akillikampusapp.ui.auth.RegisterScreen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.akillikampusapp.ui.map.MapRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    // 🔐 LOGIN

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { role ->
                                if (role == "admin") {
                                    navController.navigate("adminHome") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onNavigateToResetPassword = {
                                navController.navigate("forgetPassword")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("forgetPassword") {
                        ForgetPasswordScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 👩‍🎓 ÖĞRENCİ HOME
                    composable("home") {
                        HomeScreen(
                            onCreateNotification = {
                                navController.navigate("createNotification")
                            },
                            onViewNotifications = {
                                navController.navigate("notificationList")
                            },
                            onViewUserNotifications = {
                                navController.navigate("userNotifications")
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onOpenMap = {                     //  SADECE BU EKLENDİ
                                navController.navigate("map")
                            }
                        )
                    }

                    // ➕ BİLDİRİM OLUŞTUR (ÖĞRENCİ)
                    composable("createNotification") {
                        CreateNotificationScreen(
                            navController = navController, //EN KRİTİK SATIR
                            onDone = {
                                navController.navigate("notificationList") {
                                    popUpTo("createNotification") { inclusive = true }
                                }
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }


                    // 📄 BİLDİRİMLER (ÖĞRENCİ)
                    composable("notificationList") {
                        NotificationListScreen(
                            onBack = { navController.popBackStack() },
                            isAdmin = false
                        )
                    }

                    // 👮 ADMIN HOME
                    composable("adminHome") {
                        AdminHomeScreen(
                            onViewUsers = {
                                navController.navigate("adminUsers")
                            },
                            onViewNotifications = {
                                navController.navigate("adminNotificationList")
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("adminHome") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 📄 BİLDİRİMLER (ADMIN)
                    composable("adminNotificationList") {
                        NotificationListScreen(
                            onBack = { navController.popBackStack() },
                            isAdmin = true
                        )
                    }

                    // 👥 ADMIN – KAYITLI ÖĞRENCİLER
                    composable("adminUsers") {
                        AdminUserListScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 🔔 KULLANICIYA GELEN BİLDİRİMLER
                    composable("userNotifications") {
                        UserNotificationsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 🗺️ HARİTA EKRANI (YENİ – SADECE EKLENDİ)
                    composable(route = "map") {
                        MapRoute(
                            onDetail = { notificationId ->
                                navController.navigate("detail/$notificationId")
                            }
                        )
                    }
                    composable("map_picker") {

                        MapPickerScreen(
                            onLocationSelected = { lat, lng ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_lat", lat)

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_lng", lng)

                                navController.popBackStack()
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }



                }
            }
        }
    }
}
