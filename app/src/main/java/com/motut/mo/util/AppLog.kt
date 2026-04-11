package com.motut.mo.util

/**
 * 统一日志工具类
 * 解决 P1-8: 替换散落在代码中的 e.printStackTrace() 为结构化日志
 *
 * 使用方式：
 * - AppLog.d("tag", "调试信息")
 * - AppLog.e("tag", "错误信息", exception)
 * - AppLog.w("tag", "警告信息")
 *
 * 生产环境可通过设置 logEnabled = false 关闭所有日志输出
 */
object AppLog {
    var logEnabled = true

    fun d(tag: String, message: String) {
        if (logEnabled) android.util.Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (logEnabled) android.util.Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
        if (logEnabled) android.util.Log.w(tag, message)
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        if (logEnabled) android.util.Log.w(tag, message, throwable)
    }

    fun e(tag: String, message: String) {
        if (logEnabled) android.util.Log.e(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        if (logEnabled) android.util.Log.e(tag, message, throwable)
    }
}
