#!/bin/bash
set -e
cd "$(dirname "$0")"

echo "========================================"
echo "  宠物日记 APK 一键构建（国内镜像）"
echo "========================================"

# ---- Java ----
find_java() {
    for jdk in "$JAVA_HOME" \
        "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
        "$HOME/Documents/Codex/android-dev/jdks/jdk-17.0.19+10/Contents/Home"; do
        [ -n "$jdk" ] && [ -f "$jdk/bin/java" ] && { echo "$jdk"; return 0; }
    done
    return 1
}
export JAVA_HOME=$(find_java)
echo "✅ Java: $JAVA_HOME"

# ---- SDK ----
find_sdk() {
    for sdk in "$ANDROID_HOME" "$HOME/Library/Android/sdk" \
        "$HOME/Documents/Codex/android-dev/android-sdk"; do
        [ -d "$sdk/platforms" ] && { echo "$sdk"; return 0; }
    done
    return 1
}
export ANDROID_HOME=$(find_sdk)
echo "✅ SDK:  $ANDROID_HOME"

# ---- 清除之前可能残留的损坏下载锁 ----
echo "🧹 清理旧的 wrapper 锁文件..."
find ~/.gradle/wrapper/dists/gradle-8.5-bin -name "*.lck" -delete 2>/dev/null || true
find ~/.gradle/wrapper/dists/gradle-8.5-bin -name "*.part" -delete 2>/dev/null || true

# ---- 构建 ----
echo ""
echo "🔨 构建中（阿里云+腾讯镜像，首次约 1-2 分钟）..."
echo ""

./gradlew assembleDebug --console=plain 2>&1

# ---- 结果 ----
APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    cp "$APK" ./PetDiary.apk
    echo ""
    echo "========================================"
    echo "  ✅ 构建成功！"
    echo "  📦 $(pwd)/PetDiary.apk"
    echo "  📏 $(ls -lh PetDiary.apk | awk '{print $5}')"
    echo "========================================"
    echo ""
    echo "📱 传到 OPPO 手机安装即可"
else
    echo "❌ 构建失败"
    exit 1
fi
