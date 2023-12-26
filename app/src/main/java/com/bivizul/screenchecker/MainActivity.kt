package com.bivizul.screenchecker

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bivizul.screenchecker.ui.theme.ScreenCheckerTheme
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI(window.decorView)
        setContent {
            ScreenCheckerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val device = Build.DEVICE
    val board = Build.BOARD
    val brand = Build.BRAND
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}

fun capitalize(s: String?): String {
    if (s == null || s.length == 0) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        first.uppercaseChar().toString() + s.substring(1)
    }
}

fun gcd(width: Int, height: Int): Int {
    if (height == 0) {
        return width
    }
    return gcd(height, width % height)
}

fun hideSystemUI(view: View?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view?.windowInsetsController?.hide(
            WindowInsets.Type.statusBars()
                    or WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.systemBars()
        )
    } else {
        view?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {

    val release = Build.VERSION.RELEASE
    val api = Build.VERSION.SDK_INT
    val context = LocalContext.current
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    val dpi = LocalConfiguration.current.densityDpi

    val widthPx = width.dp.dpToPx().toInt()
    val heightPx = height.dp.dpToPx().toInt()

    val namePhone = getDeviceName()

    val gcd = gcd(width, height)

    val aspectRatio = "${width / gcd}:${height / gcd}"
    val aspectRatio2 = "${widthPx / gcd}:${heightPx / gcd}"

    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
    val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi

    val diagonal = sqrt(widthInches.pow(2) + heightInches.pow(2))

    val df = DecimalFormat("##.##")
    df.roundingMode = RoundingMode.HALF_UP
    val diagonalFormat = df.format(diagonal)

    val valuesDimens = dimensionResource(id = R.dimen.valuesDimens)

    val clipboardManager = LocalClipboardManager.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString(namePhone)) },
            text = "$namePhone",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString("$release ($api)")) },
            text = "Version - $release ($api)",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString(diagonalFormat)) },
            text = "Diagonal - $diagonalFormat",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString("${widthPx}x${heightPx}")) },
            text = "Size px - ${widthPx}x${heightPx}",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString("${width}x${height}")) },
            text = "Size dp - ${width}x${height}",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString(dpi.toString())) },
            text = "Dpi - $dpi",
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { clipboardManager.setText(AnnotatedString(valuesDimens.toString())) },
            text = "Dimens - sw${valuesDimens}",
            color = Color.White,
            fontSize = 18.sp
        )
        Image(
            modifier = Modifier
                .padding(vertical = 4.dp),
            painter = painterResource(id = R.drawable.image_density),
            contentDescription = null,
        )
    }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScreenCheckerTheme {
        Greeting()
    }
}