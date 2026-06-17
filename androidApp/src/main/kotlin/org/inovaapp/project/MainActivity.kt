package org.inovaapp.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.inovaapp.project.navigation.AppNavigation
import org.inovaapp.project.ui.theme.MindGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MindGuardTheme {
                AppNavigation()
            }
        }
    }
}