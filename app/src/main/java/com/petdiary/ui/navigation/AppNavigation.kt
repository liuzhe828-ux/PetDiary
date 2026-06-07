package com.petdiary.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.petdiary.ui.diary.DiaryEditScreen
import com.petdiary.ui.diary.DiaryListScreen
import com.petdiary.ui.diary.DiaryViewModel
import com.petdiary.ui.pet.PetScreen
import com.petdiary.ui.pet.PetViewModel
import com.petdiary.ui.tasks.TaskEditScreen
import com.petdiary.ui.tasks.TaskListScreen
import com.petdiary.ui.tasks.TaskViewModel
import com.petdiary.ui.theme.Primary
import com.petdiary.ui.theme.OnPrimary
import com.petdiary.ui.theme.Secondary

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AppNavigation(openTasks: Boolean = false) {
    val navController = rememberNavController()
    val petViewModel: PetViewModel = viewModel()
    val diaryViewModel: DiaryViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem("宠物", Icons.Filled.Pets, "pet"),
        BottomNavItem("日记", Icons.Filled.Book, "diary"),
        BottomNavItem("任务", Icons.Filled.Checklist, "tasks")
    )

    var selectedTab by rememberSaveable { mutableIntStateOf(if (openTasks) 2 else 0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.ui.graphics.Color.White
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = Primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "pet",
            modifier = Modifier.padding(padding)
        ) {
            // 宠物
            composable("pet") {
                PetScreen(viewModel = petViewModel)
            }

            // 日记列表
            composable("diary") {
                DiaryListScreen(
                    viewModel = diaryViewModel,
                    onNewEntry = {
                        navController.navigate("diary_edit/-1")
                    },
                    onEditEntry = { id ->
                        navController.navigate("diary_edit/$id")
                    }
                )
            }

            // 日记编辑
            composable(
                "diary_edit/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
                DiaryEditScreen(
                    viewModel = diaryViewModel,
                    entryId = if (entryId > 0) entryId else null,
                    onBack = { navController.popBackStack() }
                )
            }

            // 任务列表
            composable("tasks") {
                TaskListScreen(
                    viewModel = taskViewModel,
                    onNewTask = {
                        navController.navigate("task_edit/-1")
                    },
                    onEditTask = { id ->
                        navController.navigate("task_edit/$id")
                    }
                )
            }

            // 任务编辑
            composable(
                "task_edit/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
                TaskEditScreen(
                    viewModel = taskViewModel,
                    taskId = if (taskId > 0) taskId else null,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
