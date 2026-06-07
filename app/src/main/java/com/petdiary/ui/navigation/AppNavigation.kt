package com.petdiary.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.petdiary.ui.theme.OnSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(openTasks: Boolean = false) {
    val nav = rememberNavController()
    val petVM: PetViewModel = viewModel()
    val diaryVM: DiaryViewModel = viewModel()
    val taskVM: TaskViewModel = viewModel()
    var tab by remember { mutableIntStateOf(if (openTasks) 2 else 0) }

    Scaffold(bottomBar = {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            listOf("宠物" to Icons.Filled.Pets, "日记" to Icons.Filled.Book, "任务" to Icons.Filled.Checklist).forEachIndexed { i, (label, icon) ->
                NavigationBarItem(selected = tab == i, onClick = { tab = i; nav.navigate(label) { popUpTo(nav.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Icon(icon, label) }, label = { Text(label) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary, indicatorColor = Primary.copy(alpha = 0.1f)))
            }
        }
    }) { p ->
        NavHost(nav, startDestination = "宠物", Modifier.padding(p)) {
            composable("宠物") { PetScreen(viewModel = petVM) }
            composable("日记") { DiaryListScreen(viewModel = diaryVM, onNewEntry = { nav.navigate("日记编辑/-1") }, onEditEntry = { nav.navigate("日记编辑/$it") }) }
            composable("日记编辑/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                val id = it.arguments?.getLong("id") ?: -1L
                DiaryEditScreen(viewModel = diaryVM, entryId = if (id > 0) id else null, onBack = { nav.popBackStack() })
            }
            composable("任务") { TaskListScreen(viewModel = taskVM, onNewTask = { nav.navigate("任务编辑/-1") }, onEditTask = { nav.navigate("任务编辑/$it") }) }
            composable("任务编辑/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                val id = it.arguments?.getLong("id") ?: -1L
                TaskEditScreen(viewModel = taskVM, taskId = if (id > 0) id else null, onBack = { nav.popBackStack() })
            }
        }
    }
}
