package com.permopener

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent

/**
 * 无障碍服务 — 为自动化操作提供底层能力。
 *
 * 使用前需：
 * 1. 启用：通过 Shizuku 执行以下命令
 *    settings put secure enabled_accessibility_services
 *        "$(settings get secure enabled_accessibility_services):com.permopener/com.permopener.PermOpenerAccessibilityService"
 *    settings put secure accessibility_enabled 1
 *
 * 2. 或在系统设置 → 无障碍 → 已安装的应用 → PermOpener 中手动开启
 *
 * 功能：当前为空壳服务，按需在 onAccessibilityEvent 中实现具体逻辑。
 */
class PermOpenerAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 按需实现：监听界面变化、抓取控件信息等
    }

    override fun onInterrupt() {
        // 服务被中断时清理
    }
}
