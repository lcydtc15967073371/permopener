package com.permopener;

import java.lang.reflect.Method;

/**
 * Shizuku 反射工具类（必须 Java 文件）
 *
 * 提供：
 * - isAvailable()    → Shizuku.ping()
 * - isGranted()      → Shizuku.checkSelfPermission()
 * - requestPermission() → Shizuku.requestPermission()
 * - exec(cmd)        → Shizuku.newProcess("sh", null, null) + 执行命令
 */
public class ShizukuShell {

    public static boolean isAvailable() {
        try {
            Class<?> c = Class.forName("rikka.shizuku.Shizuku");
            return (boolean) c.getMethod("ping").invoke(null);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isGranted() {
        try {
            Class<?> c = Class.forName("rikka.shizuku.Shizuku");
            int r = (int) c.getMethod("checkSelfPermission").invoke(null);
            return r == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void requestPermission(int requestCode) {
        try {
            Class<?> c = Class.forName("rikka.shizuku.Shizuku");
            c.getMethod("requestPermission", int.class).invoke(null, requestCode);
        } catch (Exception ignored) {}
    }

    public static ShellResult exec(String cmd) {
        try {
            Class<?> c = Class.forName("rikka.shizuku.Shizuku");
            Method m = c.getDeclaredMethod("newProcess",
                String[].class, String[].class, String.class);
            m.setAccessible(true);
            Process proc = (Process) m.invoke(null,
                new Object[]{new String[]{"sh"}, null, null});

            // 写入命令 + 退出码标记
            java.io.OutputStream os = proc.getOutputStream();
            os.write((cmd + " 2>&1\necho \"__EXIT__:$?\"\n").getBytes());
            os.write("exit\n".getBytes());
            os.flush();

            // 读取输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(proc.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            String output = sb.toString().trim();

            // 提取退出码
            int ec = proc.waitFor();
            java.util.regex.Matcher mat = java.util.regex.Pattern
                .compile("__EXIT__:(\\d+)").matcher(output);
            if (mat.find()) {
                try { ec = Integer.parseInt(mat.group(1)); } catch (Exception ignored) {}
            }
            String clean = output.replaceAll("__EXIT__:\\d+\\s*", "").trim();
            return new ShellResult(ec, clean);
        } catch (Exception e) {
            return new ShellResult(-1, "ERR: " + e.getMessage());
        }
    }

    public static class ShellResult {
        public final int exitCode;
        public final String output;
        public ShellResult(int ec, String out) { this.exitCode = ec; this.output = out; }
    }
}
