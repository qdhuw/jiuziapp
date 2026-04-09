package com.jiuzi.jiuzijiao

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var mediaPlayerLossless: MediaPlayer? = null
    private var mediaPlayerLossy: MediaPlayer? = null
    private var isFlashing = false

    // 统计数据
    private var totalClicks = 0
    private var losslessPlays = 0
    private var lossyPlays = 0

    // UI元素
    private lateinit var backgroundImage: ImageView
    private lateinit var flashImage: ImageView
    private lateinit var statsLayout: LinearLayout
    private lateinit var hintText: TextView
    private lateinit var settingsButton: View
    private lateinit var settingsCard: CardView
    private lateinit var totalClicksText: TextView
    private lateinit var losslessText: TextView
    private lateinit var lossyText: TextView
    private lateinit var flashEffectSwitch: android.widget.Switch
    private lateinit var showStatsSwitch: android.widget.Switch
    private lateinit var showTipsSwitch: android.widget.Switch

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 全屏沉浸式
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("jiuzi_prefs", Context.MODE_PRIVATE)

        initViews()
        initMediaPlayers()
        loadSettings()
        loadStats()
        updateUI()
    }

    private fun initViews() {
        backgroundImage = findViewById(R.id.backgroundImage)
        flashImage = findViewById(R.id.flashImage)
        statsLayout = findViewById(R.id.statsLayout)
        hintText = findViewById(R.id.hintText)
        settingsButton = findViewById(R.id.settingsButton)
        settingsCard = findViewById(R.id.settingsCard)
        totalClicksText = findViewById(R.id.totalClicksText)
        losslessText = findViewById(R.id.losslessText)
        lossyText = findViewById(R.id.lossyText)
        flashEffectSwitch = findViewById(R.id.flashEffectSwitch)
        showStatsSwitch = findViewById(R.id.showStatsSwitch)
        showTipsSwitch = findViewById(R.id.showTipsSwitch)

        // 点击背景播放声音
        findViewById<View>(R.id.rootLayout).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !isFlashing) {
                onScreenClicked()
            }
            true
        }

        // 设置按钮
        settingsButton.setOnClickListener {
            settingsCard.visibility = if (settingsCard.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        // 设置开关监听
        flashEffectSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("flash_effect", isChecked).apply()
            updateUI()
        }

        showStatsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_stats", isChecked).apply()
            updateUI()
        }

        showTipsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_tips", isChecked).apply()
            updateUI()
        }
    }

    private fun initMediaPlayers() {
        mediaPlayerLossless = MediaPlayer.create(this, R.raw.sound_lossless).apply {
            setOnCompletionListener { mp ->
                mp.seekTo(0)
            }
        }

        mediaPlayerLossy = MediaPlayer.create(this, R.raw.sound_lossy).apply {
            setOnCompletionListener { mp ->
                mp.seekTo(0)
            }
        }
    }

    private fun loadSettings() {
        // 默认值
        if (!prefs.contains("flash_effect")) {
            prefs.edit().putBoolean("flash_effect", true).apply()
        }
        if (!prefs.contains("show_stats")) {
            prefs.edit().putBoolean("show_stats", true).apply()
        }
        if (!prefs.contains("show_tips")) {
            prefs.edit().putBoolean("show_tips", true).apply()
        }

        flashEffectSwitch.isChecked = prefs.getBoolean("flash_effect", true)
        showStatsSwitch.isChecked = prefs.getBoolean("show_stats", true)
        showTipsSwitch.isChecked = prefs.getBoolean("show_tips", true)
    }

    private fun loadStats() {
        totalClicks = prefs.getInt("total_clicks", 0)
        losslessPlays = prefs.getInt("lossless_plays", 0)
        lossyPlays = prefs.getInt("lossy_plays", 0)
    }

    private fun saveStats() {
        prefs.edit()
            .putInt("total_clicks", totalClicks)
            .putInt("lossless_plays", losslessPlays)
            .putInt("lossy_plays", lossyPlays)
            .apply()
    }

    private fun updateUI() {
        val showStats = prefs.getBoolean("show_stats", true)
        val showTips = prefs.getBoolean("show_tips", true)

        statsLayout.visibility = if (showStats) View.VISIBLE else View.GONE
        hintText.visibility = if (showTips) View.VISIBLE else View.GONE

        totalClicksText.text = "总点击: $totalClicks"
        losslessText.text = "无损: $losslessPlays"
        lossyText.text = "全损: $lossyPlays"
    }

    private fun onScreenClicked() {
        totalClicks++
        saveStats()
        updateUI()

        // 90% 无损，10% 全损
        val isLossless = Random.nextInt(100) < 90

        if (isLossless) {
            losslessPlays++
            playSound(mediaPlayerLossless)
        } else {
            lossyPlays++
            playSound(mediaPlayerLossy)

            // 全损时闪出效果
            if (prefs.getBoolean("flash_effect", true)) {
                triggerFlashEffect()
            }
        }

        saveStats()
        updateUI()
    }

    private fun playSound(player: MediaPlayer?) {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
            it.start()
        }
    }

    private fun triggerFlashEffect() {
        isFlashing = true
        flashImage.visibility = View.VISIBLE
        backgroundImage.visibility = View.INVISIBLE

        // 随机闪出时间 0.5-3 秒
        val flashDuration = Random.nextLong(500, 3001)

        handler.postDelayed({
            flashImage.visibility = View.GONE
            backgroundImage.visibility = View.VISIBLE
            isFlashing = false
        }, flashDuration)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerLossless?.pause()
        mediaPlayerLossy?.pause()
    }

    override fun onResume() {
        super.onResume()
        loadStats()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerLossless?.release()
        mediaPlayerLossy?.release()
        handler.removeCallbacksAndMessages(null)
    }
}
