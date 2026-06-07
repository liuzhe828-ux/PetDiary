package com.petdiary.ui.diary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.petdiary.data.DiaryData
import com.petdiary.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(viewModel: DiaryViewModel, onNewEntry: () -> Unit, onEditEntry: (Long) -> Unit) {
    val entries by viewModel.entries.collectAsState()
    var search by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        if (searching) {
            TopAppBar(title = { OutlinedTextField(value = search, onValueChange = { search = it; viewModel.searchAll(it) }, placeholder = { Text("搜索...") }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = { IconButton(onClick = { searching = false; search = ""; viewModel.searchAll("") }) { Icon(Icons.Filled.Close, "关闭") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary, titleContentColor = OnPrimary, navigationIconContentColor = OnPrimary))
        } else {
            TopAppBar(title = { Text("日记本", fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = { searching = true }) { Icon(Icons.Filled.Search, "搜索") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary, titleContentColor = OnPrimary, actionIconContentColor = OnPrimary))
        }
    }, floatingActionButton = { FloatingActionButton(onClick = onNewEntry, containerColor = Secondary) { Icon(Icons.Filled.Add, "写日记", tint = OnSecondary) } }) { padding ->
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📝", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    Spacer(Modifier.height(8.dp))
                    Text("还没有日记\n点击右下角开始记录吧", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Spacer(Modifier.height(4.dp)) }
                items(entries, key = { it.id }) { entry -> DiaryCard(entry = entry, onClick = { onEditEntry(entry.id) }, onDelete = { viewModel.deleteEntry(entry.id) }) }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun DiaryCard(entry: DiaryData, onClick: () -> Unit, onDelete: () -> Unit) {
    val fmt = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINESE)
    val moodInfo = when (entry.mood) { "happy" -> "😊 开心" to MoodHappy; "excited" -> "🤩 兴奋" to MoodExcited; "sad" -> "😢 难过" to MoodSad; "angry" -> "😠 生气" to MoodAngry; else -> "😐 平淡" to MoodNeutral }

    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(entry.title.ifBlank { "无标题" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Delete, "删除", tint = Error.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)) }
            }
            if (entry.content.isNotBlank()) { Spacer(Modifier.height(6.dp)); Text(entry.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), maxLines = 3, overflow = TextOverflow.Ellipsis) }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(moodInfo.first, style = MaterialTheme.typography.bodySmall) }, colors = AssistChipDefaults.assistChipColors(containerColor = moodInfo.second.copy(alpha = 0.15f)))
                Text(fmt.format(Date(entry.createdAt)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    }
}
