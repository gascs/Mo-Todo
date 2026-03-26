@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
@file:DependsOn("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.random.Random

// -------------------------- 【配置区】 直接修改这里的参数即可 --------------------------
val iconConfig = object {
    val text = "Mo"                     // 你要生成的文字
    val canvasSize = 1024               // 基础画布大小 (越大越清晰)
    val textColor = Color(0xFF6A5ACD)  // 文字颜色 (浅紫色)
    val bgColor = Color(0, 0, 0, 0)    // 背景颜色 (默认透明 ARGB)
    
    // 手写风格参数
    val jitterStrength = 7               // 手写抖动幅度 (0-20, 越大越潦草)
    val strokeLayers = 3                 // 笔触叠加层数 (2-5, 模拟重复描边)
    val roughEdge = true                 // 是否开启毛边效果
    
    // 字体设置
    val fontFileName = "Caveat-Bold.ttf" 
}
// ----------------------------------------------------------------------------------------

println("🚀 开始生成手写图标...")

val generator = HandwrittenIconGenerator(iconConfig, File("."))
val baseIcon = generator.createBaseIcon()

// 1. 保存一张原始大图
val outputDir = File("generated_icons")
outputDir.mkdirs()
ImageIO.write(baseIcon, "PNG", File(outputDir, "mo_original.png"))
println("✅ 原始大图已保存: generated_icons/mo_original.png")

// 2. 生成 Android 全套 mipmap 图标
generator.generateAndroidRes(baseIcon, File("app/src/main/res"))

println("\n🎉 全部完成！")

// -------------------------- 【核心算法类】 --------------------------

class HandwrittenIconGenerator(private val config: Any, private val rootDir: File) {
    
    // 使用反射读取配置
    private val text = config.javaClass.getDeclaredField("text").get(config) as String
    private val size = config.javaClass.getDeclaredField("canvasSize").get(config) as Int
    private val textColor = config.javaClass.getDeclaredField("textColor").get(config) as Color
    private val bgColor = config.javaClass.getDeclaredField("bgColor").get(config) as Color
    private val jitterStrength = config.javaClass.getDeclaredField("jitterStrength").get(config) as Int
    private val strokeLayers = config.javaClass.getDeclaredField("strokeLayers").get(config) as Int
    private val roughEdge = config.javaClass.getDeclaredField("roughEdge").get(config) as Boolean
    private val fontFileName = config.javaClass.getDeclaredField("fontFileName").get(config) as String

    // 懒加载字体
    private val font: Font by lazy {
        val fontFile = File(rootDir, "fonts/$fontFileName")
        if (fontFile.exists()) {
            val baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile)
            baseFont.deriveFont(Font.BOLD, size * 0.6f)
        } else {
            println("⚠️ 未找到字体文件 $fontFileName，使用系统默认字体。")
            Font("SansSerif", Font.ITALIC + Font.BOLD, (size * 0.6).toInt())
        }
    }

    /**
     * 核心逻辑：创建手写风格的基础图标
     */
    fun createBaseIcon(): BufferedImage {
        // 1. 初始化画布
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            color = bgColor
            fillRect(0, 0, size, size)
            color = textColor
            font = this@HandwrittenIconGenerator.font
        }

        // 2. 计算文字居中坐标
        val fm = g2d.fontMetrics
        val x = (size - fm.stringWidth(text)) / 2
        val y = (size - fm.height) / 2 + fm.ascent

        // 3. 【手写算法核心】多层叠加 + 随机抖动
        repeat(strokeLayers) {
            val offsetX = Random.nextInt(-jitterStrength, jitterStrength)
            val offsetY = Random.nextInt(-jitterStrength, jitterStrength)
            g2d.drawString(text, x + offsetX, y + offsetY)
        }
        
        g2d.dispose()

        // 4. 应用后期效果
        return if (roughEdge) applyRoughEdgeEffect(image) else image
    }

    /**
     * 效果算法：模拟铅笔/钢笔的毛边质感
     */
    private fun applyRoughEdgeEffect(img: BufferedImage): BufferedImage {
        // A. 轻微高斯模糊 (柔化边缘)
        val blurKernel = Kernel(3, 3, floatArrayOf(
            0.05f, 0.1f, 0.05f,
            0.1f,  0.4f, 0.1f,
            0.05f, 0.1f, 0.05f
        ))
        val blurred = ConvolveOp(blurKernel, ConvolveOp.EDGE_NO_OP, null).filter(img, null)

        // B. 二值化 (让边缘变得粗糙锐利)
        val result = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val threshold = 180

        for (y in 0 until size) {
            for (x in 0 until size) {
                val rgba = blurred.getRGB(x, y)
                val alpha = (rgba shr 24) and 0xff
                val gray = (rgba shr 16) and 0xff

                val newAlpha = if (gray < threshold) alpha.coerceAtLeast(220) else 0
                val finalColor = (newAlpha shl 24) or (textColor.red shl 16) or (textColor.green shl 8) or textColor.blue
                
                result.setRGB(x, y, finalColor)
            }
        }
        return result
    }

    /**
     * 生成 Android 标准 mipmap 资源
     */
    fun generateAndroidRes(baseImg: BufferedImage, resDir: File) {
        val sizes = mapOf(
            "mipmap-mdpi" to 48,
            "mipmap-hdpi" to 72,
            "mipmap-xhdpi" to 96,
            "mipmap-xxhdpi" to 144,
            "mipmap-xxxhdpi" to 192
        )

        sizes.forEach { (folder, dim) ->
            val targetDir = File(resDir, folder)
            targetDir.mkdirs()

            val scaled = BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB)
            val g = scaled.createGraphics()
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g.drawImage(baseImg, 0, 0, dim, dim, null)
            g.dispose()

            ImageIO.write(scaled, "PNG", File(targetDir, "ic_launcher.png"))
            ImageIO.write(scaled, "PNG", File(targetDir, "ic_launcher_round.png"))
            println("📦 已生成: $folder/ic_launcher.png ($dim x $dim)")
        }
    }
}
