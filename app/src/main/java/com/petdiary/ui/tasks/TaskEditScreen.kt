package com.petdiary.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petdiary.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(viewModel: TaskViewModel, taskId: Long?, onBack: () -> Unit) {
    val currentTask by viewModel.currentTask.collectAsState()
    val ctx = LocalContext.current
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableIntStateOf(1) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var reminder by remember { mutableStateOf<Long?>(null) }
    val dateFmt = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINESE)
    val cal = remember { Calendar.getInstance() }

    LaunchedEffect(taskId) { if (taskId != null && taskId > 0) viewModel.loadTask(taskId) }
    LaunchedEffect(currentTask) { currentTask?.let { title = it.title; desc = it.description; priority = it.priority; dueDate = it.dueDate; reminder = it.reminderTime } }

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (taskId != null && taskId > 0) "编辑任务" else "新建任务", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { viewModel.clearCurrent(); onBack() }) { Icon(Icons.Filled.ArrowBack, "返回") } },
            actions = { IconButton(onClick = { if (title.isNotBlank()) { viewModel.saveTask(title, desc, priority, dueDate, reminder); onBack() } else Toast.makeText(ctx, "请输入标题", Toast.LENGTH_SHORT).show() }) { Icon(Icons.Filled.Save, "保存") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary, titleContentColor = OnPrimary, navigationIconContentColor = OnPrimary, actionIconContentColor = OnPrimary))
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("任务标题 *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("描述（可选）") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            Spacer(Modifier.height(16.dp))
            Text("优先级", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "低", 2 to "中", 3 to "高").forEach { (v, label) ->
                    val color = when(v) { 3 -> PriorityHigh; 2 -> PriorityMedium; else -> PriorityLow }
                    Button(onClick = { priority = v }, colors = ButtonDefaults.buttonColors(containerColor = if (priority == v) color else color.copy(alpha = 0.15f), contentColor = if (priority == v) OnPrimary else color)) { Text(label) }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("截止日期", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { DatePickerDialog(ctx, { _, y, m, d -> cal.set(y, m, d); dueDate = cal.timeInMillis }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show() }) {
                    Icon(Icons.Filled.DateRange, null); Spacer(Modifier.width(6.dp)); Text(if (dueDate != null) dateFmt.format(Date(dueDate!!)) else "选择日期")
                }
                if (dueDate != null) { Spacer(Modifier.width(8.dp)); OutlinedButton(onClick = { dueDate = null }) { Text("清除") } }
            }
            Spacer(Modifier.height(16.dp))
            Text("提醒时间", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = {
                    val now = Calendar.getInstance()
                    DatePickerDialog(ctx, { _, y, m, d -> cal.set(y, m, d); TimePickerDialog(ctx, { _, h, mi -> cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, mi); cal.set(Calendar.SECOND, 0); reminder = cal.timeInMillis }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show() }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                }) { Icon(Icons.Filled.Notifications, null); Spacer(Modifier.width(6.dp)); Text(if (reminder != null) dateFmt.format(Date(reminder!!)) else "设置提醒") }
                if (reminder != null) { Spacer(Modifier.width(8.dp)); OutlinedButton(onClick = { reminder = null }) { Text("清除") } }
            }
        }
    }
}
