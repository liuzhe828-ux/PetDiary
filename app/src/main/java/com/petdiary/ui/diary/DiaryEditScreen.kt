package com.petdiary.ui.diary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petdiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditScreen(viewModel: DiaryViewModel, entryId: Long?, onBack: () -> Unit) {
    val currentEntry by viewModel.currentEntry.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("neutral") }

    LaunchedEffect(entryId) { if (entryId != null && entryId > 0) viewModel.loadEntry(entryId) }
    LaunchedEffect(currentEntry) { currentEntry?.let { title = it.title; content = it.content; mood = it.mood } }

    val moods = listOf("happy" to "😊 开心", "excited" to "🤩 兴奋", "neutral" to "😐 平淡", "sad" to "😢 难过", "angry" to "😠 生气")

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (entryId != null && entryId > 0) "编辑日记" else "写日记", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { viewModel.clearCurrent(); onBack() }) { Icon(Icons.Filled.ArrowBack, "返回") } },
            actions = { IconButton(onClick = { viewModel.saveEntry(title, content, mood); onBack() }) { Icon(Icons.Filled.Save, "保存") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary, titleContentColor = OnPrimary, navigationIconContentColor = OnPrimary, actionIconContentColor = OnPrimary))
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("今天的心情", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { (v, label) ->
                    FilterChip(selected = mood == v, onClick = { mood = v }, label = { Text(label, style = MaterialTheme.typography.bodySmall) })
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("今天发生了什么...") }, modifier = Modifier.fillMaxWidth().height(300.dp), maxLines = 20)
        }
    }
}
