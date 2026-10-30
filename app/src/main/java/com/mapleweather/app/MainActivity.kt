package com.mapleweather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFFF7F8FC)
private val Muted = Color(0xFFD3DCE9)
private val Glass = Color(0x35FFFFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MapleWeatherApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapleWeatherApp() {
    var page by remember { mutableStateOf("天气") }
    var city by remember { mutableStateOf("上海市") }
    var menu by remember { mutableStateOf(false) }
    val gradient = Brush.verticalGradient(listOf(Color(0xFF284C69), Color(0xFF557A91), Color(0xFF9E8796)))
    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFFFB347), background = Color(0xFF284C69))) {
        Scaffold(containerColor = Color.Transparent, bottomBar = {
            NavigationBar(containerColor = Color(0xEE294B65)) {
                listOf("天气" to Icons.Default.WbSunny, "定位显示" to Icons.Default.LocationOn, "天气预警" to Icons.Default.Notifications, "天气源管理" to Icons.Default.Cloud, "关于" to Icons.Default.Info).forEach { (label, icon) ->
                    NavigationBarItem(selected = page == label, onClick = { page = label }, icon = { Icon(icon, null) }, label = { Text(label, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFFFC15E), selectedTextColor = Color(0xFFFFC15E), unselectedIconColor = Muted, unselectedTextColor = Muted))
                }
            }
        }) { padding ->
            Box(Modifier.fillMaxSize().background(gradient).padding(padding)) {
                when (page) {
                    "天气" -> WeatherHome(city, onCity = { menu = true })
                    "定位显示" -> LocationPage()
                    "天气预警" -> AlertPage()
                    "天气源管理" -> SourcesPage()
                    else -> AboutPage()
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    listOf("上海市", "北京市", "杭州市").forEach { name -> DropdownMenuItem(text = { Text(name) }, onClick = { city = name; menu = false }) }
                    DropdownMenuItem(text = { Text("+ 添加城市") }, onClick = { city = "杭州市"; menu = false })
                }
            }
        }
    }
}

@Composable
private fun WeatherHome(city: String, onCity: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text(city, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.SemiBold); Text("定位已更新 · 17:50", color = Muted, fontSize = 12.sp) }
                IconButton(onClick = onCity) { Icon(Icons.Default.LocationCity, "切换城市", tint = Ink) }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 4.dp)) {
                Text("20°", color = Ink, fontSize = 88.sp, lineHeight = 92.sp, fontWeight = FontWeight.Light)
                Row(verticalAlignment = Alignment.CenterVertically) { Text("晴朗", color = Ink, fontSize = 20.sp); Spacer(Modifier.width(12.dp)); Text("体感 19°  ·  高 22° / 低 15°", color = Muted, fontSize = 13.sp) }
                Text("空氣清新，适合外出走走", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
        item { GlassCard("整点天气", "未来 24 小时", Icons.Default.Schedule) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                listOf("现在" to "20°", "19:00" to "19°", "20:00" to "17°", "21:00" to "17°", "22:00" to "16°", "23:00" to "15°").forEach { (time, temp) -> Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(time, color = Muted, fontSize = 12.sp); Text("☀", color = Color(0xFFFFC15E), fontSize = 22.sp, modifier = Modifier.padding(vertical = 8.dp)); Text(temp, color = Ink, fontWeight = FontWeight.SemiBold) } }
            }
        } }
        item { GlassCard("未来 7 天", "气温趋势", Icons.Default.CalendarMonth) {
            listOf("今天" to "22° / 15°", "周四" to "23° / 16°", "周五" to "21° / 14°", "周六" to "19° / 13°", "周日" to "20° / 14°", "周一" to "22° / 15°", "周二" to "24° / 16°").forEachIndexed { i, pair ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(pair.first, Modifier.weight(1f), color = Ink); Text(if (i == 2) "🌧" else "☁", modifier = Modifier.weight(.6f)); Text(pair.second, color = Muted) }
            }
        } }
        item { GlassCard("日出日落", "日照时长 11小时12分钟", Icons.Default.WbTwilight) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Metric("日出", "06:02"); Text("日落还有 01:29", color = Muted, modifier = Modifier.align(Alignment.CenterVertically)); Metric("日落", "17:14") } } }
        item { GlassCard("空气质量", "优 · AQI 28", Icons.Default.Air) { Text("空气很好，可正常活动，快去呼吸新鲜空气吧。", color = Muted); Spacer(Modifier.height(12.dp)); Box(Modifier.fillMaxWidth().height(8.dp).background(Brush.horizontalGradient(listOf(Color(0xFF52C878), Color(0xFFFFC247), Color(0xFFE35968))), CircleShape)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { MetricCard("湿度", "46%", Modifier.weight(1f)); MetricCard("能见度", "10 km", Modifier.weight(1f)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { MetricCard("气压", "1,014 hPa", Modifier.weight(1f)); MetricCard("风力", "东北风 9 km/h", Modifier.weight(1f)) } }
    }
}

@Composable
private fun GlassCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Glass, RoundedCornerShape(24.dp)).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color(0xFFFFC15E), modifier = Modifier.size(19.dp)); Spacer(Modifier.width(8.dp)); Text(title, color = Ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f)); Text(subtitle, color = Muted, fontSize = 11.sp) }
        Spacer(Modifier.height(16.dp)); content()
    }
}

@Composable
private fun Metric(label: String, value: String) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(label, color = Muted, fontSize = 12.sp); Text(value, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold) } }

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) { Column(modifier.background(Glass, RoundedCornerShape(22.dp)).padding(16.dp)) { Text(title, color = Muted); Spacer(Modifier.height(18.dp)); Text(value, color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold) } }

@Composable
private fun SimplePage(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(title, color = Ink, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted)
        Spacer(Modifier.height(4.dp))
        Column(Modifier.fillMaxWidth().background(Glass, RoundedCornerShape(24.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun LocationPage() = SimplePage("定位显示", "管理定位精度与收藏城市") {
    Text("定位精度", color = Ink, fontWeight = FontWeight.SemiBold)
    var selected by remember { mutableStateOf("省电") }
    listOf("高精度", "省电", "仅网络定位").forEach { option -> Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected == option, onClick = { selected = option }); Text(option, color = Ink) } }
    HorizontalDivider(color = Color.White.copy(alpha = .2f)); Text("收藏城市", color = Ink, fontWeight = FontWeight.SemiBold)
    listOf("上海市 · 当前城市", "北京市", "杭州市").forEach { Text("📍  $it", color = Muted) }
    OutlinedButton(onClick = {}) { Text("+ 添加城市") }
}

@Composable
private fun AlertPage() = SimplePage("天气预警", "设置提醒方式和通知时间") {
    var rain by remember { mutableStateOf(true) }; var unusual by remember { mutableStateOf(true) }; var morning by remember { mutableStateOf(true) }; var evening by remember { mutableStateOf(true) }
    SettingSwitch("雨雪预警", rain) { rain = it }; SettingSwitch("异常天气提醒", unusual) { unusual = it }
    HorizontalDivider(color = Color.White.copy(alpha = .2f)); Text("早晚天气通知", color = Ink, fontWeight = FontWeight.SemiBold)
    SettingSwitch("早间通知 · 07:30", morning) { morning = it }; SettingSwitch("晚间通知 · 20:30", evening) { evening = it }
    Text("点击时间可自定义（原型）", color = Muted, fontSize = 12.sp)
}

@Composable
private fun SourcesPage() = SimplePage("天气源管理", "Open-Meteo 默认启用，失败时自动回退") {
    SourceRow("Open-Meteo", "默认 · 无需 API Key", true)
    SourceRow("彩云天气", "输入 API Key 后可用", false)
    OutlinedTextField(value = "", onValueChange = {}, label = { Text("彩云天气 API Key") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    SourceRow("和风天气", "输入 API Key 后可用", false)
    OutlinedTextField(value = "", onValueChange = {}, label = { Text("和风天气 API Key") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedButton(onClick = {}) { Text("测试连接并保存") }
    Text("失败回退顺序：Open-Meteo → 已启用数据源 → 最近缓存", color = Muted, fontSize = 12.sp)
}

@Composable
private fun AboutPage() = SimplePage("关于枫叶天气", "清爽、可信赖的天气信息") {
    Text("版本 1.0.0 · Android 12+", color = Ink)
    Text("天气数据来源：Open-Meteo 及用户配置的数据服务。", color = Muted)
    Text("低功耗模式", color = Ink, fontWeight = FontWeight.SemiBold)
    var lowPower by remember { mutableStateOf(false) }; SettingSwitch("减弱动画与后台活动", lowPower) { lowPower = it }
    Text("原型阶段暂不包含隐私政策与发布材料。", color = Muted, fontSize = 12.sp)
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Ink); Switch(checked, onCheckedChange = onChecked) } }

@Composable
private fun SourceRow(name: String, detail: String, active: Boolean) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(name, color = Ink, fontWeight = FontWeight.Medium); Text(detail, color = Muted, fontSize = 12.sp) }; if (active) Text("使用中", color = Color(0xFF9FE0B0), fontSize = 12.sp) else Text("未配置", color = Muted, fontSize = 12.sp) } }
