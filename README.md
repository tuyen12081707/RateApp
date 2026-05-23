# Panda Rate Library

Thư viện hiển thị hộp thoại Đánh giá (Rating Dialog) thông minh cho Android, tích hợp sẵn Google Play In-App Review, Gửi Feedback qua Email và Trình quản lý luồng hiển thị qua Firebase Remote Config.

## 🌟 Tính năng nổi bật

* **Logic phân luồng thông minh:** * 1 - 3 Sao: Chuyển hướng người dùng gửi Email Feedback.
    * 4 - 5 Sao: Kích hoạt Google Play In-App Review API.
* **Quản lý hiển thị tự động:** Tự động lưu trạng thái `isRated`, đảm bảo không làm phiền người dùng sau khi họ đã đánh giá.
* **Hỗ trợ Remote Config:** Dễ dàng kiểm soát thời điểm hiển thị thông qua chuỗi cấu hình (VD: Hiển thị ở lần thứ `1, 3, 5, 7`).
* **Tích hợp Firebase Analytics:** Tự động log các sự kiện quan trọng (`rate_show`, `rate_submit`, `rate_cancel`).
* **Tùy biến cao:** Hỗ trợ thay đổi màu sắc, text, hành vi dismiss dialog.

---

## 🚀 Hướng dẫn sử dụng

### 1. Sử dụng với Điều kiện tự động (Khuyên dùng)

Thư viện cung cấp `RateConditionHelper` giúp bạn dễ dàng kết hợp với Firebase Remote Config để quyết định thời điểm hiển thị Dialog dựa trên số lần người dùng thực hiện một hành động (Ví dụ: Mở app, Back ra màn hình home, v.v.).

**Ví dụ:** Hiển thị rate khi user view màn Highlight lần thứ 1, 3, 5, 7...

```kotlin
import com.panda.ratelib.RateConditionHelper
import com.panda.ratelib.RateConfig
import com.panda.ratelib.RateDialog

// 1. Lấy chuỗi config từ Firebase Remote Config
// Giả sử giá trị trên Firebase là "1,3,5,7,9,11,13"
val highlightConfigStr = remoteConfig.getString("rate_home_highlights_view1_3_5_7_9_11_13")

// 2. Kiểm tra điều kiện (Hàm này sẽ tự động đếm số lần gọi)
val shouldShow = RateConditionHelper.shouldShowRate(
    context = this,
    eventKey = "event_highlight_view", // Key duy nhất cho hành động này
    configString = highlightConfigStr
)

// 3. Hiển thị Dialog
if (shouldShow) {
    val config = RateConfig.Builder()
        .setFeedbackEmail("support@yourdomain.com")
        .build()
        
    RateDialog(this, config).show()
}
