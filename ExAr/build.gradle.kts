// تعيين رقم إصدار البلجن (يجب أن يكون رقماً صحيحاً)
version = 1

cloudstream {
    // الخصائص الأساسية ليتعرف عليها سكريبت البناء تلقائياً
    description = "My Custom Arabic Extension"
    authors = listOf("Bash4QI")
    
    // تحديد نوع المحتوى
    tvTypes = listOf("Movies", "TvSeries")
    
    // تفعيل التوافقية عبر المنصات
    isCrossPlatform = true
}
