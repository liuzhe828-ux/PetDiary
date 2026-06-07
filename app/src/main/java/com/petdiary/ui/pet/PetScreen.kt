package com.petdiary.ui.pet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petdiary.ui.theme.Primary
import com.petdiary.ui.theme.Secondary

@Composable
fun PetScreen(viewModel: PetViewModel) {
    val petState by viewModel.pet.collectAsState()
    val msg by viewModel.message.collectAsState()
    val displayMsg = msg
    var showNameDialog by remember { mutableStateOf(false) }

    // 跳动动画
    val scale by animateFloatAsState(
        targetValue = if (displayMsg != null) 1.05f else 1f,
        animationSpec = tween(300),
        label = "pet_bounce"
    )

    // 心情表情
    val moodEmoji = when {
        petState.happiness >= 80 -> "😄"
        petState.happiness >= 50 -> "🙂"
        petState.happiness >= 20 -> "😐"
        else -> "😢"
    }

    val hungerEmoji = when {
        petState.hunger >= 80 -> "🍔"
        petState.hunger >= 50 -> "🍪"
        petState.hunger >= 20 -> "🥺"
        else -> "😫"
    }

    // 宠物类型表情
    val petEmoji = when (petState.petType) {
        "cat" -> "🐱"
        "dog" -> "🐶"
        "bunny" -> "🐰"
        else -> "🐱"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部标题
        Text(
            text = "桌面宠物",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 宠物展示卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 宠物名字
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = petState.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "改名",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showNameDialog = true },
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Text(
                    text = "Lv.${petState.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 宠物形象
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E0FF))
                        .border(3.dp, Primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = petEmoji,
                        fontSize = 72.sp
                    )
                }

                // 宠物消息
                if (displayMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = displayMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 状态条
                StatusBar(
                    label = "饱腹度 $hungerEmoji",
                    value = petState.hunger,
                    color = Color(0xFFFF8A65)
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusBar(
                    label = "快乐度 $moodEmoji",
                    value = petState.happiness,
                    color = Color(0xFF66BB6A)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.feed() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A65))
                    ) {
                        Icon(Icons.Filled.Restaurant, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("喂食")
                    }
                    Button(
                        onClick = { viewModel.play() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("玩耍")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 宠物类型切换
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "选择宠物",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PetTypeOption("🐱", "猫咪", "cat", petState.petType) { viewModel.changeType(it) }
                    PetTypeOption("🐶", "小狗", "dog", petState.petType) { viewModel.changeType(it) }
                    PetTypeOption("🐰", "兔子", "bunny", petState.petType) { viewModel.changeType(it) }
                }
            }
        }
    }

    // 改名对话框
    if (showNameDialog) {
        NameDialog(
            currentName = petState.name,
            onConfirm = {
                viewModel.changeName(it)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false }
        )
    }
}

@Composable
private fun StatusBar(label: String, value: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$value/100",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun PetTypeOption(
    emoji: String,
    label: String,
    type: String,
    selectedType: String,
    onSelect: (String) -> Unit
) {
    val isSelected = type == selectedType
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect(type) }
            .padding(12.dp)
    ) {
        Text(text = emoji, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("给宠物起个名字") },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名字") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text("确定")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
