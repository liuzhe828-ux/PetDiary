package com.petdiary.ui.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petdiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditScreen(
    viewModel: DiaryViewModel,
    entryId: Long?,
    onBack: () -> Unit
) {
    val currentEntry by viewModel.currentEntry
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("neutral") }

    LaunchedEffect(entryId) {
        if (entryId != null && entryId > 0) {
            viewModel.loadEntry(entryId)
        }
    }

    LaunchedEffect(currentEntry) {
        currentEntry?.let {
            title = it.title
            content = it.content
            mood = it.mood
        }
    }

    val moodOptions = listOf(
        "happy" to "😊 开心",
        "excited" to "🤩 兴奋",
        "neutral" to "😐 平淡",
        "sad" to "😢 难过",
        "angry" to "😠 生气"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (entryId != null && entryId > 0) "编辑日记" else "写日记",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearCurrentEntry()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveEntry(title, content, mood)
                        onBack()
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "保存")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = OnPrimary,
                    navigationIconContentColor = OnPrimary,
                    actionIconContentColor = OnPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 心情选择
            Text(
                text = "今天的心情",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moodOptions.forEach { (value, label) ->
                    FilterChip(
                        selected = mood == value,
                        onClick = { mood = value },
                        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (value) {
                                "happy" -> MoodHappy.copy(alpha = 0.3f)
                                "excited" -> MoodExcited.copy(alpha = 0.3f)
                                "sad" -> MoodSad.copy(alpha = 0.3f)
                                "angry" -> MoodAngry.copy(alpha = 0.3f)
                                else -> MoodNeutral.copy(alpha = 0.3f)
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 标题
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 内容
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("今天发生了什么...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                maxLines = 20
            )
        }
    }
}
