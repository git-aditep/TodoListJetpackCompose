package com.example.todolistjetpackcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SettingsScreen: หน้าจอการตั้งค่าหลักของแอปพลิเคชัน
 * แสดงรายการเมนูต่างๆ เช่น Help & Support และ About
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    // กำหนดสีหลักและสถานะการแสดงผลของ Modal
    val primaryPurple = Color(0xFF7B61FF)
    var showHelpModal by remember { mutableStateOf(false) }

    // 1. ตรวจสอบและแสดง Dialog สำหรับ Help & Support เมื่อสถานะเป็น true
    if (showHelpModal) {
        HelpSupportDialog(
            primaryColor = primaryPurple,
            onDismiss = { showHelpModal = false }
        )
    }

    Scaffold(
        topBar = { SettingsTopBar(onBack) }
    ) { innerPadding ->
        // 2. แสดงรายการเมนูการตั้งค่าทั้งหมด
        SettingsList(
            innerPadding = innerPadding,
            primaryColor = primaryPurple,
            onHelpClick = { showHelpModal = true }
        )
    }
}

// --- ส่วนประกอบย่อยของ UI (Sub-Composables) ---

/**
 * HelpSupportDialog: หน้าต่างแจ้งเตือน (Dialog) สำหรับแสดงคำถามที่พบบ่อยและข้อมูลติดต่อ
 */
@Composable
private fun HelpSupportDialog(primaryColor: Color, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = primaryColor) },
        title = { Text(text = "Help & Support", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Frequently Asked Questions", style = MaterialTheme.typography.titleSmall, color = primaryColor)
                Text(text = "• How to add a task?\nGo to the home screen and tap the purple '+' button.", fontSize = 14.sp)
                Text(text = "• How to delete a task?\nClick on any task card and click delete task to remove it.", fontSize = 14.sp)
                HorizontalDivider(thickness = 0.5.dp)
                Text(text = "Contact Us", style = MaterialTheme.typography.titleSmall, color = primaryColor)
                Text(text = "Facebook FanPage : Basic Web and Android Developer.", fontSize = 14.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = primaryColor, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * SettingsTopBar: แถบเครื่องมือด้านบน พร้อมชื่อหน้าจอและปุ่มย้อนกลับ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Setting", style = MaterialTheme.typography.titleLarge) },
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

/**
 * SettingsList: รายการเมนูการตั้งค่าแบบเลื่อนได้ (LazyColumn)
 */
@Composable
private fun SettingsList(
    innerPadding: PaddingValues,
    primaryColor: Color,
    onHelpClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        // ส่วนหัวข้อกลุ่มการตั้งค่า
        item { SettingHeader("General", primaryColor) }
        // การ์ดรวมรายการเมนูย่อย
        item {
            SettingCard {
                Column {
                    // เมนู Help & Support
                    SettingItem(
                        title = "Help & Support",
                        subtitle = "FAQs and Contact",
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        iconBgColor = Color(0xFF4CAF50),
                        onClick = onHelpClick
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    // เมนูข้อมูลเกี่ยวกับแอป (About)
                    SettingItem(
                        title = "About",
                        subtitle = "Version 1.0.0",
                        icon = Icons.Default.Info,
                        iconBgColor = Color(0xFF2196F3),
                        onClick = { /* จัดการเมื่อมีการคลิก */ }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

/**
 * SettingHeader: ข้อความหัวข้อสำหรับกลุ่มเมนู
 */
@Composable
private fun SettingHeader(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

/**
 * SettingCard: การ์ดพื้นหลังสำหรับบรรจุรายการเมนูเพื่อให้ดูเป็นหมวดหมู่
 */
@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

/**
 * SettingItem: แถวรายการเมนูแต่ละรายการ พร้อมไอคอน ชื่อ และรายละเอียด
 */
@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ส่วนแสดงไอคอนพร้อมพื้นหลังวงกลม
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconBgColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ส่วนแสดงชื่อเมนูและคำอธิบายย่อย
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // ส่วนท้ายของแถว (ถ้ามี)
        trailing?.invoke()
    }
}
