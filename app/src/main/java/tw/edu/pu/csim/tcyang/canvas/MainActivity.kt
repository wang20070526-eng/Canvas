package tw.edu.pu.csim.tcyang.canvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.sp
import tw.edu.pu.csim.tcyang.canvas.ui.theme.CanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TestCanvas(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TestCanvas(modifier: Modifier = Modifier) {
    val minionsImage: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.minions)

    // 用於記錄畫布寬高
    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    // 遊戲狀態與移動進度 (0.0f ~ 1.0f)
    var isPlaying by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }

    // 動畫迴圈：控制小小兵移動速度與原地暫停/接續
    LaunchedEffect(isPlaying) {
        var lastTime = 0L
        val durationNanos = 6_000_000_000L // 6 秒跑完一趟

        while (isPlaying) {
            withFrameNanos { frameTime ->
                if (lastTime != 0L) {
                    val frameDelta = frameTime - lastTime
                    progress = (progress + frameDelta.toFloat() / durationNanos) % 1.0f
                }
                lastTime = frameTime
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 顯示畫布寬高資訊
        Text(
            text = "畫布寬度 $canvasWidth, 長度 $canvasHeight",
            fontSize = 18.sp
        )

        // 按鈕點擊切換狀態
        Button(onClick = { isPlaying = !isPlaying }) {
            Text(if (isPlaying) "停止遊戲" else "繼續遊戲")
        }

        // Canvas 畫布
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // 更新畫布尺寸
            canvasWidth = size.width
            canvasHeight = size.height

            // 1. 起點為左上角 (0, 0)，終點為右下角
            val startX = 0f
            val startY = 0f
            val endX = size.width
            val endY = size.height

            // 2. 繪製洋紅色的圓
            drawCircle(
                color = Color.Magenta,
                radius = 100f,
                center = Offset(400f, 800f)
            )

            // 3. 繪製藍色空心矩形
            drawRect(
                color = Color.Blue,
                topLeft = Offset(400f, 200f),
                size = Size(200f, 200f),
                style = Stroke(width = 5f)
            )

            // 4. 繪製紅色的線 (從左上角 (0,0) 畫到右下角)
            drawLine(
                color = Color.Red,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 10f
            )

            // 5. 根據進度計算當前座標
            val minionX = startX + (endX - startX) * progress
            val minionY = startY + (endY - startY) * progress

            // 繪製小小兵
            translate(
                left = minionX - (minionsImage.width / 2f),
                top = minionY - (minionsImage.height / 2f)
            ) {
                drawImage(minionsImage)
            }
        }
    }
}